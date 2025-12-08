package orinnetwork.jpstudy.application.admin.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import orinnetwork.jpstudy.domain.exam.BlueprintDetail;

@Schema(description = "시험 Blueprint 세부 구성 요소 응답 DTO (어떤 카테고리에서 몇 문제 출제할지)")
public record BlueprintDetailResponse(
        @Schema(description = "문제 출제에 사용할 카테고리의 고유 ID")
        Long categoryId,

        @Schema(description = "카테고리 이름 (예: N3 문법)")
        String categoryName,

        @Schema(description = "해당 카테고리에서 출제할 문제 수")
        int count,

        @Schema(description = "시험 내에서 이 파트가 출제되는 순서")
        int sequence
) {
    public static BlueprintDetailResponse from(BlueprintDetail detail) {
        return new BlueprintDetailResponse(
                detail.getCategory().getId(),
                detail.getCategory().getName(),
                detail.getQuestionCount(),
                detail.getSequence()
        );
    }
}