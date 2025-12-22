package orinnetwork.jpstudy.application.progress.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "단어 학습 세션 응답 DTO (복습 카드와 새 카드를 포함)")
public class StudySessionResponse {

    @Schema(description = "이번 세션에 복습할 단어 카드 목록")
    private final List<WordCard> reviewCards;

    @Schema(description = "이번 세션에 새로 학습할 단어 카드 목록")
    private final List<WordCard> newCards;

    public StudySessionResponse(List<WordCard> reviewCards, List<WordCard> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}