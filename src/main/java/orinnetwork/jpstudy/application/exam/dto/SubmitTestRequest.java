package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "시험 답안 최종 제출 요청 DTO")
public record SubmitTestRequest(
        @NotEmpty(message = "답안 목록은 필수 항목입니다.")
        @Schema(description = "사용자가 각 문제에 대해 선택한 답안 목록 (필수). 시험의 모든 문제에 대한 답을 포함해야 합니다.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<UserAnswer> answers
) {
}