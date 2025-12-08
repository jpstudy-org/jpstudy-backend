package orinnetwork.jpstudy.application.admin.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "관리자용 1:1 문의 답변 등록 요청 DTO")
public record AnswerInquiryRequest(
        @NotBlank(message = "답변 내용은 필수 입력 항목입니다.")
        @Schema(
                description = "문의에 대한 답변 내용 (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "문의하신 사항에 대해 확인 후 처리 완료했습니다."
        )
        String content
) {
}