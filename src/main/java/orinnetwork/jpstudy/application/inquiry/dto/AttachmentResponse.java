package orinnetwork.jpstudy.application.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import orinnetwork.jpstudy.domain.inquiry.InquiryAttachment;

@Schema(description = "문의 첨부 파일 정보 응답 DTO")
public record AttachmentResponse(
        @Schema(description = "첨부 파일의 고유 ID")
        Long id,

        @Schema(description = "사용자가 업로드한 원래 파일명")
        String originalFileName,

        @Schema(description = "파일 크기 (Byte)")
        long fileSize,

        @Schema(description = "클라우드 스토리지에 저장된 파일 키/경로")
        String storageKey,

        @Schema(description = "파일 다운로드를 위한 임시 서명된 URL (Pre-signed URL)")
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
