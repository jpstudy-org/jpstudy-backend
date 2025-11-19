package orinnetwork.jpstudy.application.admin.exam.dto;

import orinnetwork.jpstudy.domain.exam.BlueprintDetail;

public record BlueprintDetailResponse(
        Long categoryId,
        String categoryName,
        int count,
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