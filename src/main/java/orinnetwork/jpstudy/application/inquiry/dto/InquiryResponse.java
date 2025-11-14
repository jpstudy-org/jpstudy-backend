package orinnetwork.jpstudy.application.inquiry.dto;

import java.time.LocalDateTime;
import java.util.List;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;

public record InquiryResponse(
        Long id,
        Long memberId,
        String title,
        String content,
        InquiryStatus status,
        LocalDateTime createdAt,
        List<AttachmentResponse> attachments
) {
    public static InquiryResponse from(Inquiry entity) {
        List<AttachmentResponse> attachmentResponses = entity.getAttachments().stream()
                .map(AttachmentResponse::from)
                .toList();

        return new InquiryResponse(
                entity.getId(),
                entity.getMemberId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                attachmentResponses
        );
    }
}
