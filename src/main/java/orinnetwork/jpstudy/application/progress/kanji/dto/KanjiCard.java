package orinnetwork.jpstudy.application.progress.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import orinnetwork.jpstudy.application.progress.common.dto.IntervalPreview;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
@Schema(description = "한자 학습 세션에서 사용되는 개별 한자 카드 DTO")
public class KanjiCard {

    @Schema(description = "한자의 고유 ID")
    private final Long kanjiId;

    @Schema(description = "한자 본체 (예: 覚)")
    private final String character;

    @Schema(description = "한자의 의미 (사용자 설정 언어에 맞춰 반환됨)")
    private final String meaning;

    @Schema(description = "음독 (중국어 발음 기반의 읽는 법)")
    private final String onyomi;

    @Schema(description = "훈독 (일본어 고유어 발음 기반의 읽는 법)")
    private final String kunyomi;

    @Schema(description = "각 난이도 선택 시 다음 복습 예정 시간 미리보기")
    private final IntervalPreview intervalPreview;

    public KanjiCard(Kanji kanji, String lang, IntervalPreview intervalPreview) {
        this.kanjiId = kanji.getId();
        this.character = kanji.getCharacter();
        this.onyomi = kanji.getOnyomi();
        this.kunyomi = kanji.getKunyomi();
        this.intervalPreview = intervalPreview;

        if ("en".equalsIgnoreCase(lang) || "jp".equalsIgnoreCase(lang)) {
            this.meaning = kanji.getMeaningEn();
        } else {
            this.meaning = kanji.getMeaning();
        }
    }
}
