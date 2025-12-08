package orinnetwork.jpstudy.presentation.admin.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.member.AdminMemberService;
import orinnetwork.jpstudy.application.admin.member.dto.MemberAdminResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.member.MemberStatus;

@Tag(name = "Admin - Member", description = "관리자: 회원 목록 조회 및 제재 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "회원 목록 검색 및 조회", description = "키워드와 상태에 따라 회원을 검색하고 결과를 페이지네이션하여 반환합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<MemberAdminResponse>> getMembers(
            @Parameter(description = "검색 키워드 (이메일, 닉네임 등)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "회원 상태 필터 (예: ACTIVE, BANNED)")
            @RequestParam(required = false) MemberStatus status,

            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<MemberAdminResponse> response = adminMemberService.getMembers(keyword, status, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 영구/일시 제재 (밴)", description = "특정 회원을 영구 또는 지정된 기간 동안 제재(밴) 처리합니다.")
    @PostMapping("/{memberId}/ban")
    public ResponseEntity<Void> banMember(
            @Parameter(description = "제재할 회원 ID")
            @PathVariable Long memberId,

            @Parameter(description = "제재 기간 (일 단위). 생략 시 영구 제재")
            @RequestParam(required = false) Integer days
    ) {
        adminMemberService.banMember(memberId, days);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 제재 해제 (언밴)", description = "특정 회원의 제재(밴)를 해제하고 상태를 ACTIVE로 변경합니다.")
    @PostMapping("/{memberId}/unban")
    public ResponseEntity<Void> unbanMember(
            @Parameter(description = "제재를 해제할 회원 ID")
            @PathVariable Long memberId
    ) {
        adminMemberService.unbanMember(memberId);
        return ResponseEntity.ok().build();
    }
}
