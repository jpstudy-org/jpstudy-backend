package orinnetwork.jpstudy.application.image.dto;

public record GeneratePresignedUrlResponse(
        String presignedUrl,
        String formFields
) {
}