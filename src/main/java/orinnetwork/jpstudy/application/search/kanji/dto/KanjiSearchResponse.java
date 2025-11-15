package orinnetwork.jpstudy.application.search.kanji.dto;

import lombok.Getter;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
public class KanjiSearchResponse {

    private final String character;
    private final String meaning;
    private final String onyomi;
    private final String kunyomi;
    private final int strokeCount;
    private final String radical;

    public KanjiSearchResponse(Kanji kanji, String lang) {
        this.character = kanji.getCharacter();
        this.onyomi = kanji.getOnyomi();
        this.kunyomi = kanji.getKunyomi();
        this.strokeCount = kanji.getStrokeCount();
        this.radical = kanji.getRadical();

        if ("en".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            this.meaning = kanji.getMeaningEn();
        }
        else {
            this.meaning = kanji.getMeaning();
        }
    }
}