package orinnetwork.jpstudy.application.progress.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class StudySessionResponse {

    private final List<KanjiCardDto> reviewCards;
    private final List<KanjiCardDto> newCards;

    public StudySessionResponse(List<KanjiCardDto> reviewCards, List<KanjiCardDto> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}