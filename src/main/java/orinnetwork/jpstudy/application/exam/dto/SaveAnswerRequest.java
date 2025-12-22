package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "시험 응시 중 답안 임시 저장 요청 DTO")
public record SaveAnswerRequest(
        @NotNull
        @Schema(description = "답안을 저장할 문제의 고유 ID (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
        Long questionId,

        @NotNull
        @Schema(description = "사용자가 선택한 선택지의 고유 ID (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "405")
        Long choiceId
) {
}