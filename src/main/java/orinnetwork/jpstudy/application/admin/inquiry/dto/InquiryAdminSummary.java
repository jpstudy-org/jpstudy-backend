package orinnetwork.jpstudy.application.admin.inquiry.dto;

import java.time.LocalDateTime;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;
import orinnetwork.jpstudy.domain.member.Member;

public record InquiryAdminSummary(
        Long id,
        String title,
        String memberEmail,   // 작성자 이메일 (연락용)
        String memberName,    // 작성자 이름
        InquiryStatus status, // 답변완료/대기중
        LocalDateTime createdAt,
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