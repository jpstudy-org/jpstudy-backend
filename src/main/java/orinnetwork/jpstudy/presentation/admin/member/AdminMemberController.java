package orinnetwork.jpstudy.presentation.admin.member;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<CustomPageResponse<MemberAdminResponse>> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<MemberAdminResponse> response = adminMemberService.getMembers(keyword, status, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{memberId}/ban")
    public ResponseEntity<Void> banMember(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer days
    ) {
        adminMemberService.banMember(memberId, days);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/unban")
    public ResponseEntity<Void> unbanMember(@PathVariable Long memberId) {
        adminMemberService.unbanMember(memberId);
        return ResponseEntity.ok().build();
    }
}
