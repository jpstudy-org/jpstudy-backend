package orinnetwork.jpstudy.application.admin.exam.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.exam.ExamBlueprint;

public record BlueprintResponse(
        Long id,
        String title,
        String description,
        Long levelId,
        String levelName,
        int totalTimeMinutes,
        List<BlueprintDetailResponse> details
) {
    public static BlueprintResponse from(ExamBlueprint blueprint) {
        List<BlueprintDetailResponse> detailResponses = blueprint.getDetails().stream()
                .map(BlueprintDetailResponse::from)
                .toList();

        return new BlueprintResponse(
                blueprint.getId(),
                blueprint.getTitle(),
                blueprint.getDescription(),
                blueprint.getLevel().getId(),
                blueprint.getLevel().getName(),
                blueprint.getTotalTimeMinutes(),
                detailResponses
        );
    }
}