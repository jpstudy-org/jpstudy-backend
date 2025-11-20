package orinnetwork.jpstudy.application.admin.member.dto;

import java.time.LocalDateTime;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberStatus;
import orinnetwork.jpstudy.domain.member.Role;

public record MemberAdminResponse (
        Long id,
        String email,
        String username,
        Role role,
        MemberStatus status,        // 정상, 정지 등 상태
        LocalDateTime banExpiresAt, // 정지 해제일 (없으면 null)
        int level,
        long experience,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {
    public static MemberAdminResponse from(Member member) {
        return new MemberAdminResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getRole(),
                member.getStatus(),
                member.getBanExpiresAt(), // Getter 필요
                member.getLevel(),
                member.getExperience(),
                member.getCreatedAt(),
                null
        );
    }
}
