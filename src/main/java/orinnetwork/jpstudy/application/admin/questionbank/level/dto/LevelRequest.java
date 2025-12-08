package orinnetwork.jpstudy.application.admin.questionbank.level.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "문제 은행 난이도 레벨(Level) 생성/수정 요청 DTO")
public record LevelRequest(
        @NotBlank(message = "레벨 이름은 필수입니다.")
        @Schema(
                description = "새로운 난이도 레벨의 이름 (예: N3, N2 등) (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "N3"
        )
        String name
) {
}