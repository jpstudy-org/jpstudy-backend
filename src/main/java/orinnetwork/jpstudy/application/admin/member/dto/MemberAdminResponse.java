package orinnetwork.jpstudy.application.admin.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberStatus;
import orinnetwork.jpstudy.domain.member.Role;

@Schema(description = "관리자용 회원 상세 정보 응답 DTO")
public record MemberAdminResponse (
        @Schema(description = "회원의 고유 ID")
        Long id,

        @Schema(description = "회원 이메일 (계정 식별자)")
        String email,

        @Schema(description = "사용자 이름/닉네임")
        String username,

        @Schema(description = "회원의 권한 (예: USER, ADMIN)")
        Role role,

        @Schema(description = "회원의 현재 상태 (예: NORMAL, SUSPENDED, WITHDRAWN)")
        MemberStatus status,

        @Schema(description = "계정 정지 해제 일시 (정지 상태가 아닐 경우 null)", nullable = true)
        LocalDateTime banExpiresAt,

        @Schema(description = "현재 학습 레벨")
        int level,

        @Schema(description = "총 누적 경험치")
        long experience,

        @Schema(description = "회원 가입 시각")
        LocalDateTime createdAt,

        @Schema(description = "최근 로그인 시각 (정보가 없을 경우 null)", nullable = true)
        LocalDateTime lastLoginAt
) {
    public static MemberAdminResponse from(Member member) {
        return new MemberAdminResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getRole(),
                member.getStatus(),
                member.getBanExpiresAt(),
                member.getLevel(),
                member.getExperience(),
                member.getCreatedAt(),
                null
        );
    }
}
