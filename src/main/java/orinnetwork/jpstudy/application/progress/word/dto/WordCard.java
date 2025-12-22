package orinnetwork.jpstudy.application.progress.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
@Schema(description = "단어 학습 세션에서 사용되는 개별 단어 카드 DTO")
public class WordCard {

    @Schema(description = "단어의 고유 ID")
    private final Long wordId;

    @Schema(description = "단어의 본체 (예: 覚える)")
    private final String term;

    @Schema(description = "단어의 읽는 법 (히라가나/가타카나, 예: おぼえる)")
    private final String reading;

    @Schema(description = "단어의 의미 목록 (사용자 설정 언어에 맞춰 반환됨)")
    private final List<String> meanings;

    public WordCard(Word word, String lang) {
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
