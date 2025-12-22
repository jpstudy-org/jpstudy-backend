package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiResponse;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
@Schema(description = "사전 단어 상세 정보 응답 DTO (관련 한자, 의미, 태그 포함)")
public class WordResponse {

    @Schema(description = "단어의 고유 ID")
    private final Long id;

    @Schema(description = "단어의 표기 (한자 포함)", example = "日本語")
    private final String term;

    @Schema(description = "단어의 읽는 법 (히라가나/가타카나)", example = "にほんご")
    private final String reading;

    @Schema(description = "단어의 난이도 레벨 (예: JLPT 레벨)")
    private final int level;

    @Schema(description = "단어를 구성하는 관련 한자 목록")
    private final List<KanjiResponse> kanjis;

    @Schema(description = "단어의 의미 및 예문 목록")
    private final List<MeaningResponse> meanings;

    @Schema(description = "단어에 적용된 태그 목록")
    private final List<TagResponse> tags;

    @Builder
    public WordResponse(Long id, String term, String reading, int level, List<KanjiResponse> kanjis,
                        List<MeaningResponse> meanings, List<TagResponse> tags) {
        this.id = id;
        this.term = term;
        this.reading = reading;
        this.level = level;
        this.kanjis = kanjis;
        this.meanings = meanings;
        this.tags = tags;
    }

    public static WordResponse from(Word word) {
        List<KanjiResponse> kanjiResponses = word.getWordKanjis().stream()
                .map(wordKanji -> KanjiResponse.from(wordKanji.getKanji()))
                .toList();

        List<MeaningResponse> meaningResponses = word.getMeanings().stream()
                .map(MeaningResponse::from)
                .toList();

        List<TagResponse> tagResponses = word.getWordTags().stream()
                .map(wordTag -> TagResponse.from(wordTag.getTag()))
                .toList();

        return WordResponse.builder()
                .id(word.getId())
                .term(word.getTerm())
                .reading(word.getReading())
                .level(word.getLevel())
                .kanjis(kanjiResponses) // 연결된 한자 목록 포함
                .meanings(meaningResponses)
                .tags(tagResponses)
                .build();
    }
}