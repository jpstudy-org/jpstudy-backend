package orinnetwork.jpstudy.application.inquiry.dto;

import orinnetwork.jpstudy.domain.inquiry.InquiryAttachment;

public record AttachmentResponse(
        Long id,
        String originalFileName,
        long fileSize,
        String storageKey
) {
    public static AttachmentResponse from(InquiryAttachment entity) {
        return new AttachmentResponse(
                entity.getId(),
                entity.getOriginalFileName(),
                entity.getFileSize(),
                entity.getStorageKey()
        );
    }
}
