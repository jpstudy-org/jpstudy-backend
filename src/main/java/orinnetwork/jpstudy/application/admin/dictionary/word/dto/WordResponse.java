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
    private final String meaning;
    private final String meaningEn;
    private final int level;
    private final List<KanjiResponse> kanjis;

    @Builder
    public WordResponse(Long id, String term, String reading, String meaning, String meaningEn, int level, List<KanjiResponse> kanjis) {
        this.id = id;
        this.term = term;
        this.reading = reading;
        this.meaning = meaning;
        this.meaningEn = meaningEn;
        this.level = level;
        this.kanjis = kanjis;
    }

    public static WordResponse from(Word word) {
        List<KanjiResponse> kanjiResponses = word.getWordKanjis().stream()
                .map(wordKanji -> KanjiResponse.from(wordKanji.getKanji()))
                .toList();

        return WordResponse.builder()
                .id(word.getId())
                .term(word.getTerm())
                .reading(word.getReading())
                .meaning(word.getMeaning())
                .meaningEn(word.getMeaningEn())
                .level(word.getLevel())
                .kanjis(kanjiResponses) // 연결된 한자 목록 포함
                .build();
    }
}