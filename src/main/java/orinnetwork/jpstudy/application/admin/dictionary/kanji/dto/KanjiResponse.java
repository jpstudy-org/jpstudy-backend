package orinnetwork.jpstudy.application.admin.dictionary.kanji.dto;

import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
public class KanjiResponse {

    private final String character;
    private final String meaning;
    private final String meaningEn;
    private final String onyomi;
    private final String kunyomi;
    private final int strokeCount;
    private final String radical;
    private final int level;

    @Builder
    public KanjiResponse(String character, String meaning, String meaningEn, String onyomi, String kunyomi,
                         int strokeCount, String radical, int level) {
        this.character = character;
        this.meaning = meaning;
        this.meaningEn = meaningEn;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.strokeCount = strokeCount;
        this.radical = radical;
        this.level = level;
    }

    public static KanjiResponse from(Kanji kanji) {
        return KanjiResponse.builder()
                .character(kanji.getCharacter())
                .meaning(kanji.getMeaning())
                .meaningEn(kanji.getMeaningEn())
                .onyomi(kanji.getOnyomi())
                .kunyomi(kanji.getKunyomi())
                .strokeCount(kanji.getStrokeCount())
                .radical(kanji.getRadical())
                .level(kanji.getLevel())
                .build();
    }
}