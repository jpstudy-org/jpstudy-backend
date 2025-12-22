package orinnetwork.jpstudy.application.admin.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.exam.ExamBlueprint;

@Schema(description = "관리자용 시험 Blueprint(설계도) 상세 조회 응답 DTO")
public record BlueprintResponse(
        @Schema(description = "Blueprint의 고유 ID")
        Long id,

        @Schema(description = "시험 Blueprint의 제목")
        String title,

        @Schema(description = "시험 Blueprint에 대한 상세 설명", nullable = true)
        String description,

        @Schema(description = "시험이 속한 난이도 레벨의 고유 ID")
        Long levelId,

        @Schema(description = "시험이 속한 난이도 레벨의 이름")
        String levelName,

        @Schema(description = "총 응시 가능 시간 (분 단위)")
        int totalTimeMinutes,

        @Schema(description = "시험을 구성하는 문제 영역별 상세 설정 목록")
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