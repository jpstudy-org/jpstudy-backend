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

    public KanjiCardDto(Kanji kanji) {
        this.kanjiId = kanji.getId();
        this.character = kanji.getCharacter();
        this.meaning = kanji.getMeaning();
        this.onyomi = kanji.getOnyomi();
        this.kunyomi = kanji.getKunyomi();
    }
}