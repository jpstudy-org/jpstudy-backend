package orinnetwork.jpstudy.application.inquiry.dto;

public record AttachmentRequest(
        String storageKey,
        String originalFileName,
        long fileSize,
        String contentType
) {
}