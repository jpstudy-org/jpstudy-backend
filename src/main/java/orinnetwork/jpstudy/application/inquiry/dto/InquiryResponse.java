package orinnetwork.jpstudy.application.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;

@Schema(description = "1:1 문의 상세 조회 응답 DTO (답변 포함)")
public record InquiryResponse(
        @Schema(description = "문의의 고유 ID")
        Long id,

        @Schema(description = "문의 작성자 회원 ID")
        Long memberId,

        @Schema(description = "문의 제목")
        String title,

        @Schema(description = "문의 본문 내용")
        String content,

        @Schema(description = "문의 처리 상태 (예: PENDING, COMPLETED)")
        InquiryStatus status,

        @Schema(description = "문의 작성 시각")
        LocalDateTime createdAt,

        @Schema(description = "문의에 첨부된 파일 목록", nullable = true)
        List<AttachmentResponse> attachments,

        @Schema(description = "관리자의 답변 내용", nullable = true)
        String answerContent,

        @Schema(description = "답변 완료 시각", nullable = true)
        LocalDateTime answeredAt
) {
    public static InquiryResponse of(Inquiry entity, List<AttachmentResponse> attachmentResponses) {
        return new InquiryResponse(
                entity.getId(),
                entity.getMemberId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                attachmentResponses,
                entity.getAnswerContent(),
                entity.getAnsweredAt()
        );
    }
}
