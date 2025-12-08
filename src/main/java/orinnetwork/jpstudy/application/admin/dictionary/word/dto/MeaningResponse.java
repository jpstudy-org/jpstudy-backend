package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Meaning;

@Getter
@Schema(description = "사전 단어의 의미 정보 응답 DTO (한국어, 영어 포함)")
public class MeaningResponse {

    @Schema(description = "단어의 한국어 의미", example = "일본어")
    private final String meaningKr;

    @Schema(description = "단어의 영어 의미", example = "Japanese language")
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
