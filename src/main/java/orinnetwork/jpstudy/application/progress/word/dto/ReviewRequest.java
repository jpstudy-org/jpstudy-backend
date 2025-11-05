package orinnetwork.jpstudy.application.progress.word.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRequest {

    @NotNull(message = "한자 ID 필수")
    private Long wordId;

    @NotNull(message = "학습 난이도 평가 필수")
    private ReviewDifficulty difficulty;

    public ReviewRequest(Long wordId, ReviewDifficulty difficulty) {
        this.wordId = wordId;
        this.difficulty = difficulty;
    }
}
