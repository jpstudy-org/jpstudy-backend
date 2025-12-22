package orinnetwork.jpstudy.application.admin.questionbank.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;

@Schema(description = "문제 은행 카테고리 정보 응답 DTO (상위 섹션 정보 포함)")
public record CategoryResponse(
        @Schema(description = "카테고리의 고유 ID")
        Long id,

        @Schema(description = "카테고리의 이름 (예: N3 문법, N4 어휘)")
        String name,

        @Schema(description = "카테고리가 속한 상위 섹션의 ID")
        Long sectionId,

        @Schema(description = "카테고리가 속한 상위 섹션의 이름")
        String sectionName
) {
    public static CategoryResponse fromEntity(QuestionCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSection().getId(),
                category.getSection().getName()
        );
    }
}