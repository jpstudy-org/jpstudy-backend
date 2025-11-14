package orinnetwork.jpstudy.application.progress.kanji.dto;

import lombok.Getter;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
public class KanjiCardDto {

    private final Long kanjiId;
    private final String character;
    private final String meaning;
    private final String onyomi;
    private final String kunyomi;

    public KanjiCardDto(Kanji kanji, String lang) {
        this.kanjiId = kanji.getId();
        this.character = kanji.getCharacter();
        this.onyomi = kanji.getOnyomi();
        this.kunyomi = kanji.getKunyomi();

        if ("en".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            this.meaning = kanji.getMeaningEn();
        }
        else {
            this.meaning = kanji.getMeaning();
        }
    }
}