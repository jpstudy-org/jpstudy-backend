package orinnetwork.jpstudy.application.admin.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리자용 시험 Blueprint 생성 요청 DTO")
public record CreateExamRequest(
        @NotNull(message = "레벨 ID는 필수입니다.")
        @Schema(
                description = "시험이 속할 난이도 레벨의 고유 ID (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "3"
        )
        Long levelId,

        @NotBlank(message = "시험 제목은 필수입니다.")
        @Schema(
                description = "시험의 제목 (예: JLPT N3 모의고사 1회) (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "JLPT N3 모의고사 1회"
        )
        String title
) {
}