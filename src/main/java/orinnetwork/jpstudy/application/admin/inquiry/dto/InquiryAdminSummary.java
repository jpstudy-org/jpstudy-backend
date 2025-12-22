package orinnetwork.jpstudy.application.admin.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;
import orinnetwork.jpstudy.domain.member.Member;

@Schema(description = "관리자용 1:1 문의 목록 요약 응답 DTO")
public record InquiryAdminSummary(
        @Schema(description = "문의의 고유 ID")
        Long id,

        @Schema(description = "문의 제목")
        String title,

        @Schema(description = "작성자 이메일 (연락 및 식별용)")
        String memberEmail,

        @Schema(description = "작성자 이름/닉네임 (탈퇴 시 '탈퇴한 회원')")
        String memberName,

        @Schema(description = "문의 처리 상태 (PENDING: 답변 대기, COMPLETED: 답변 완료)")
        InquiryStatus status,

        @Schema(description = "문의 작성 시각")
        LocalDateTime createdAt,

        @Schema(description = "답변 완료 시각 (답변이 완료되지 않았으면 null)", nullable = true)
        LocalDateTime answeredAt
) {
    public static InquiryAdminSummary of(Inquiry inquiry, Member member) {
        return new InquiryAdminSummary(
                inquiry.getId(),
                inquiry.getTitle(),
                member != null ? member.getEmail() : "(알 수 없음)", // 탈퇴 회원 대응
                member != null ? member.getUsername() : "(탈퇴한 회원)",
                inquiry.getStatus(),
                inquiry.getCreatedAt(),
                inquiry.getAnsweredAt()
        );
    }
}