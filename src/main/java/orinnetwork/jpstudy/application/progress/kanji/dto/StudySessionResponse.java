package orinnetwork.jpstudy.application.progress.kanji.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class StudySessionResponse {

    private final List<KanjiCard> reviewCards;
    private final List<KanjiCard> newCards;

    public StudySessionResponse(List<KanjiCard> reviewCards, List<KanjiCard> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}