package orinnetwork.jpstudy.application.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드를 위한 Pre-signed URL 발급 요청 DTO")
public record GeneratePresignedUrlRequest(
        @Schema(description = "업로드할 파일의 원래 파일명 (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "profile_image.png")
        String fileName,

        @Schema(description = "파일 크기 (Byte 단위, 필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "102400")
        long fileSize,

        @Schema(description = "파일의 MIME 타입 (예: image/png, application/pdf)", requiredMode = Schema.RequiredMode.REQUIRED, example = "image/png")
        String contentType
) {
}