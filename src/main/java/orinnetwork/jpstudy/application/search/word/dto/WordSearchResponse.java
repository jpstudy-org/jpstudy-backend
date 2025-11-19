package orinnetwork.jpstudy.application.search.word.dto;

import java.util.List;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
public class WordSearchResponse {
    private final String term;
    private final String reading;
    private final List<String> meanings;

    public WordSearchResponse(Word word, String lang) {
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
