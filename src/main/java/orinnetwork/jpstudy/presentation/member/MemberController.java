package orinnetwork.jpstudy.presentation.member;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.member.MemberService;
import orinnetwork.jpstudy.application.member.dto.MemberProfileResponse;
import orinnetwork.jpstudy.application.member.dto.MemberUpdateRequest;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;
import orinnetwork.jpstudy.presentation.auth.AuthResponseHelper;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthResponseHelper authResponseHelper;

    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getMemberId();
        MemberProfileResponse profile = memberService.getMyProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateRequest request
            ) {
        Long userId = userDetails.getMemberId();
        memberService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdrawMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response
    ) {
        Long userId = userDetails.getMemberId();
        memberService.withdrawMember(userId);

        authResponseHelper.clearCookies(response);

        return ResponseEntity.ok().build();
    }
}
