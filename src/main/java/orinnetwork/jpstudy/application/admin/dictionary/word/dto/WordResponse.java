package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiResponse;
import orinnetwork.jpstudy.domain.word.Word;

@Getter
public class WordResponse {

    private final Long id;
    private final String term;
    private final String reading;
    private final int level;
    private final List<KanjiResponse> kanjis;
    private final List<MeaningResponse> meanings;
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