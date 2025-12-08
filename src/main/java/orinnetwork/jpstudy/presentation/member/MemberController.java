package orinnetwork.jpstudy.presentation.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.member.MemberService;
import orinnetwork.jpstudy.application.member.dto.MemberProfileResponse;
import orinnetwork.jpstudy.application.member.dto.MemberUpdateRequest;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.presentation.auth.AuthResponseHelper;

@Tag(name = "Member API", description = "사용자 계정 정보 관리 (프로필 조회/수정/탈퇴)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthResponseHelper authResponseHelper;

    @Operation(summary = "내 프로필 조회", description = "로그인된 사용자의 상세 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getMemberId();
        MemberProfileResponse profile = memberService.getMyProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "내 프로필 수정", description = "로그인된 사용자의 프로필 정보(닉네임, 언어 설정 등)를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid @RequestBody MemberUpdateRequest request
    ) {
        Long userId = userDetails.getMemberId();
        memberService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "로그인된 사용자의 계정을 삭제하고 로그아웃 처리합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdrawMember(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(hidden = true)
            HttpServletResponse response
    ) {
        Long userId = userDetails.getMemberId();
        memberService.withdrawMember(userId);

        authResponseHelper.clearCookies(response);

        return ResponseEntity.ok().build();
    }
}
