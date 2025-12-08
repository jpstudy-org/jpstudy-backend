package orinnetwork.jpstudy.application.admin.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Schema(description = "관리자용 시험 Blueprint(설계도) 생성 요청 DTO")
public record CreateBlueprintRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Schema(
                description = "시험 Blueprint의 제목 (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "JLPT N3 모의고사 2회차"
        )
        String title,

        @Schema(
                description = "시험 Blueprint에 대한 상세 설명",
                nullable = true,
                example = "언어지식(문자·어휘), 언어지식(문법) 및 독해, 청해 파트로 구성됨."
        )
        String description,

        @NotNull(message = "레벨 ID는 필수입니다.")
        @Schema(
                description = "시험이 속할 난이도 레벨의 고유 ID (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "3"
        )
        Long levelId,

        @Positive(message = "시험 시간은 양수여야 합니다.")
        @Schema(
                description = "총 응시 가능 시간 (분 단위, 필수)",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "105" // 1시간 45분
        )
        int totalTimeMinutes,

        @NotNull(message = "문제 구성 목록은 필수입니다.")
        @Schema(
                description = "시험을 구성할 문제 영역별 상세 설정 목록 (필수)",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        List<BlueprintDetailRequest> details
) {
}