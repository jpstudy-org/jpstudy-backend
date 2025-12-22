package orinnetwork.jpstudy.application.search.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
@Schema(description = "한자 검색 결과 응답 DTO")
public class KanjiSearchResponse {

    @Schema(description = "한자 본체 (예: 覚)")
    private final String character;

    @Schema(description = "한자의 의미 (사용자 설정 언어에 맞춰 반환됨)")
    private final String meaning;

    @Schema(description = "음독 (중국어 발음 기반의 읽는 법, 예: カク)")
    private final String onyomi;

    @Schema(description = "훈독 (일본어 고유어 발음 기반의 읽는 법, 예: おぼ.える)")
    private final String kunyomi;

    @Schema(description = "총 획수")
    private final int strokeCount;

    @Schema(description = "부수 (한자의 기본 구성 요소, 예: 心)")
    private final String radical;

    public KanjiSearchResponse(Kanji kanji, String lang) {
        this.character = kanji.getCharacter();
        this.onyomi = kanji.getOnyomi();
        this.kunyomi = kanji.getKunyomi();
        this.strokeCount = kanji.getStrokeCount();
        this.radical = kanji.getRadical();

        if ("en".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            this.meaning = kanji.getMeaningEn();
        } else {
            this.meaning = kanji.getMeaning();
        }
    }
}