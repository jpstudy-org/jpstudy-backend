package orinnetwork.jpstudy.application.progress.word.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class StudySessionResponse {

    private final List<WordCard> reviewCards;
    private final List<WordCard> newCards;

    public StudySessionResponse(List<WordCard> reviewCards, List<WordCard> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}