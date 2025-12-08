package orinnetwork.jpstudy.application.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "클라우드 스토리지 직접 업로드를 위한 Pre-signed URL 및 폼 필드 응답 DTO")
public record GeneratePresignedUrlResponse(
        @Schema(description = "파일 업로드를 수행할 임시 서명된 URL (PUT 요청 대상)", requiredMode = Schema.RequiredMode.REQUIRED)
        String presignedUrl,

        @Schema(description = "POST 업로드 시 사용해야 하는 추가 폼 필드 (JSON 문자열 형태)", requiredMode = Schema.RequiredMode.REQUIRED)
        String formFields
) {
}