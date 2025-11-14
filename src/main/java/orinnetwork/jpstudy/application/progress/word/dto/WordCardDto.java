package orinnetwork.jpstudy.application.progress.word.dto;

import java.util.List;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
public class WordCardDto {

    private final Long wordId;
    private final String term;
    private final String reading;
    private final List<String> meanings;

    public WordCardDto(Word word, String lang) {
        this.wordId = word.getId();
        this.term = word.getTerm();
        this.reading = word.getReading();

        if ("en".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            this.meanings = word.getMeanings().stream()
                    .map(Meaning::getMeaningEn)
                    .toList();
        } else {
            this.meanings = word.getMeanings().stream()
                    .map(Meaning::getMeaningKr)
                    .toList();
        }
    }
}
