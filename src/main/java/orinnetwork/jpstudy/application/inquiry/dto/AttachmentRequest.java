package orinnetwork.jpstudy.application.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문의 생성 시 첨부 파일 정보를 담는 요청 DTO")
public record AttachmentRequest(
        @Schema(description = "파일이 임시 저장된 클라우드 스토리지 키/경로 (사전 업로드 필요)", requiredMode = Schema.RequiredMode.REQUIRED)
        String storageKey,

        @Schema(description = "사용자가 업로드한 원래 파일명 (필수)", requiredMode = Schema.RequiredMode.REQUIRED)
        String originalFileName,

        @Schema(description = "파일 크기 (Byte 단위, 필수)", requiredMode = Schema.RequiredMode.REQUIRED)
        long fileSize,

        @Schema(description = "파일의 MIME 타입 (예: image/png, application/pdf)", requiredMode = Schema.RequiredMode.REQUIRED)
        String contentType
) {
}