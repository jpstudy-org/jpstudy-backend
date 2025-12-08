package orinnetwork.jpstudy.application.progress.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "한자 학습 세션 응답 DTO (복습 카드와 새 카드를 포함)")
public class StudySessionResponse {

    @Schema(description = "이번 세션에 복습할 한자 카드 목록")
    private final List<KanjiCard> reviewCards;

    @Schema(description = "이번 세션에 새로 학습할 한자 카드 목록")
    private final List<KanjiCard> newCards;

    public StudySessionResponse(List<KanjiCard> reviewCards, List<KanjiCard> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}