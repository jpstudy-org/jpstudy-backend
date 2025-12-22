package orinnetwork.jpstudy.application.admin.questionbank.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "문제 은행 카테고리 생성/수정 요청 DTO")
public record CategoryRequest(
        @NotNull(message = "섹션 ID는 필수입니다.")
        @Schema(
                description = "카테고리가 속할 상위 섹션의 고유 ID (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1"
        )
        Long sectionId,

        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Schema(
                description = "새로운 카테고리의 이름 (예: 독해 N3, 문법 N4 등) (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "문법 N3"
        )
        String name
) {
}