package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "사용자의 개별 문제 답안 요청 DTO")
public record UserAnswer(
        @NotNull
        @Schema(description = "답변을 제출하는 문제의 고유 ID (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
        Long questionId,

        @NotNull
        @Schema(description = "사용자가 선택한 선택지의 고유 ID (필수)", requiredMode = Schema.RequiredMode.REQUIRED, example = "405")
        Long selectedChoiceId
) {
}