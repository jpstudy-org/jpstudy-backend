package orinnetwork.jpstudy.application.search.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
@Schema(description = "단어 검색 결과 응답 DTO")
public class WordSearchResponse {

    @Schema(description = "검색된 단어의 본체 (예: 覚える)")
    private final String term;

    @Schema(description = "단어의 읽는 법 (히라가나/가타카나, 예: おぼえる)")
    private final String reading;

    @Schema(description = "단어의 의미 목록 (사용자 설정 언어에 맞춰 반환됨)")
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
