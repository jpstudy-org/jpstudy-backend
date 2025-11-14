package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;

@Getter
public class MeaningResponse {
    private final String meaningKr;
    private final String meaningEn;

    @Builder
    public MeaningResponse(String meaningKr, String meaningEn) {
        this.meaningKr = meaningKr;
        this.meaningEn = meaningEn;
    }

    public static MeaningResponse from(Meaning meaning) {
        return MeaningResponse.builder()
                .meaningKr(meaning.getMeaningKr())
                .meaningEn(meaning.getMeaningEn())
                .build();
    }
}
