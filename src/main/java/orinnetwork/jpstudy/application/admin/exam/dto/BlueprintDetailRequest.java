package orinnetwork.jpstudy.application.admin.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "시험 Blueprint 세부 구성 요소 요청 DTO (카테고리별 문제 수 및 순서 지정)")
public record BlueprintDetailRequest(
        @NotNull(message = "카테고리 ID는 필수입니다.")
        @Schema(
                description = "문제 출제에 사용할 카테고리의 고유 ID (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "10"
        )
        Long categoryId,

        @Positive(message = "문제 수는 1 이상이어야 합니다.")
        @Schema(
                description = "해당 카테고리에서 출제할 문제 수 (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "20"
        )
        int count,

        @Positive(message = "순서는 1 이상이어야 합니다.")
        @Schema(
                description = "시험 내에서 이 파트가 출제되는 순서 (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1"
        )
        int sequence
) {
}