package orinnetwork.jpstudy.application.admin.questionbank.category.dto;

import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;

public record CategoryResponse(
        Long id,
        String name,
        Long sectionId,
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