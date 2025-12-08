package orinnetwork.jpstudy.application.admin.dictionary.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
@Schema(description = "사전 한자 상세 정보 응답 DTO")
public class KanjiResponse {

    @Schema(description = "한자 문자 자체", example = "日")
    private final String character;

    @Schema(description = "한자의 한국어 의미/뜻", example = "날, 해")
    private final String meaning;

    @Schema(description = "한자의 영어 의미/뜻", example = "day, sun")
    private final String meaningEn;

    @Schema(description = "한자의 음독 (Onyomi, 가타카나 표기)", example = "ニチ、ジツ")
    private final String onyomi;

    @Schema(description = "한자의 훈독 (Kunyomi, 히라가나 표기)", example = "ひ、-び、-か")
    private final String kunyomi;

    @Schema(description = "한자의 총 획수", example = "4")
    private final int strokeCount;

    @Schema(description = "한자의 부수", example = "日")
    private final String radical;

    @Schema(description = "한자의 난이도 레벨 (예: JLPT 또는 교육 한자 레벨)", example = "3")
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