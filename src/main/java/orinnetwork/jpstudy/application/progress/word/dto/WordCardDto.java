package orinnetwork.jpstudy.application.progress.word.dto;

import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
public class WordCardDto {

    private final Long wordId;
    private final String term;
    private final String reading;
    private final String meaning;

    public WordCardDto(Word word) {
        this.wordId = word.getId();
        this.term = word.getTerm();
        this.reading = word.getReading();
        this.meaning = word.getMeaning();
    }
}
