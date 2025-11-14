package orinnetwork.jpstudy.application.image.dto;

public record GeneratePresignedUrlRequest(
        String fileName,
        long fileSize,
        String contentType
) { }