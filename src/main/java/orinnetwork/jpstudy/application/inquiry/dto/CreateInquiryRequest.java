package orinnetwork.jpstudy.application.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "1:1 문의 생성 요청 DTO")
public record CreateInquiryRequest(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Schema(description = "문의 제목 (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "로그인 관련 오류 문의 드립니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        @Schema(description = "문의 본문 내용 (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "어제부터 갑자기 로그인이 안 됩니다. 스크린샷 첨부합니다.")
        String content,

        @Schema(description = "문의에 첨부할 파일 목록 (선택 사항)", nullable = true)
        List<AttachmentRequest> attachments
) {
}