package orinnetwork.jpstudy.application.progress.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewDifficulty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "한자 복습 결과 제출 요청 DTO")
public class ReviewRequest {

    @NotNull(message = "한자 ID 필수")
    @Schema(description = "복습 결과를 제출할 한자 ID (필수)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long kanjiId;

    @NotNull(message = "학습 난이도 평가 필수")
    @Schema(description = "사용자가 평가한 복습 난이도 (필수). [EASY, GOOD, HARD, AGAIN 중 택 1]", requiredMode = Schema.RequiredMode.REQUIRED)
    private ReviewDifficulty difficulty;

    public ReviewRequest(Long kanjiId, ReviewDifficulty difficulty) {
        this.kanjiId = kanjiId;
        this.difficulty = difficulty;
    }
}
