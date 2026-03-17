package orinnetwork.jpstudy.application.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "클라우드 스토리지 직접 업로드를 위한 Pre-signed URL 응답 DTO")
public record GeneratePresignedUrlResponse(
        @Schema(description = "파일 업로드를 수행할 임시 서명된 URL (PUT 요청 대상)", requiredMode = Schema.RequiredMode.REQUIRED)
        String presignedUrl,

        @Schema(description = "스토리지에 저장되는 고유 파일명 (서버 전달 시 이 값을 사용)", requiredMode = Schema.RequiredMode.REQUIRED)
        String storageKey
) {
}
