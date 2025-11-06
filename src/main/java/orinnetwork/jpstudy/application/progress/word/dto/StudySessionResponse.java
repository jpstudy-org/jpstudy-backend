package orinnetwork.jpstudy.application.progress.word.dto;

import java.util.List;
import lombok.Getter;
import orinnetwork.jpstudy.application.progress.kanji.dto.KanjiCardDto;

@Getter
public class StudySessionResponse {

    private final List<WordCardDto> reviewCards;
    private final List<WordCardDto> newCards;

    public StudySessionResponse(List<WordCardDto> reviewCards, List<WordCardDto> newCards) {
        this.reviewCards = reviewCards;
        this.newCards = newCards;
    }
}