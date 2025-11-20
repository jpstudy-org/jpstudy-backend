package orinnetwork.jpstudy.application.inquiry.dto;

import orinnetwork.jpstudy.domain.inquiry.InquiryAttachment;

public record AttachmentResponse(
        Long id,
        String originalFileName,
        long fileSize,
        String storageKey,
        String presignedUrl
) {
    public static AttachmentResponse of(InquiryAttachment entity, String presignedUrl) {
        return new AttachmentResponse(
                entity.getId(),
                entity.getOriginalFileName(),
                entity.getFileSize(),
                entity.getStorageKey(),
                presignedUrl
        );
    }
}
