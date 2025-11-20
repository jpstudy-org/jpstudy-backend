package orinnetwork.jpstudy.application.admin.exam.dto;

import java.util.List;

public record CreateBlueprintRequest(
        String title,
        String description,
        Long levelId,
        int totalTimeMinutes,
        List<BlueprintDetailRequest> details
) {
}
