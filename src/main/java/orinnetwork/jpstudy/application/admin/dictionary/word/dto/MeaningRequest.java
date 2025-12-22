package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "사전 단어의 의미 정보 요청 DTO")
public class MeaningRequest {

    @NotBlank(message = "한국어 의미 입력은 필수입니다.")
    @Schema(
            description = "단어의 한국어 의미 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "일본어"
    )
    private String meaningKr;

    @Schema(
            description = "단어의 영어 의미 (선택 사항)",
            nullable = true,
            example = "Japanese language"
    )
    private String meaningEn;
}