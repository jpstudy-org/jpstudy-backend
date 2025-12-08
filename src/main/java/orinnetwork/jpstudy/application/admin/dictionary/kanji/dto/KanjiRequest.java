package orinnetwork.jpstudy.application.admin.dictionary.kanji.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
@NoArgsConstructor
@Schema(description = "사전 한자 생성 및 수정 요청 DTO")
public class KanjiRequest {

    @NotBlank(message = "한자 입력은 필수입니다")
    @Schema(
            description = "한자 문자 자체 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "日"
    )
    private String character;

    @NotBlank(message = "뜻 입력은 필수입니다")
    @Schema(
            description = "한자의 한국어 의미/뜻 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "날, 해"
    )
    private String meaning;

    @NotBlank(message = "영문 뜻 입력은 필수입니다")
    @Schema(
            description = "한자의 영어 의미/뜻 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "day, sun"
    )
    private String meaningEn;

    @NotBlank(message = "음독 입력은 필수입니다")
    @Schema(
            description = "한자의 음독 (Onyomi, 가타카나 표기) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ニチ、ジツ"
    )
    private String onyomi;

    @NotBlank(message = "훈독 입력은 필수입니다")
    @Schema(
            description = "한자의 훈독 (Kunyomi, 히라가나 표기) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ひ、-び、-か"
    )
    private String kunyomi;

    @Positive(message = "획수는 0보다 커야 합니다.")
    @Schema(
            description = "한자의 총 획수 (필수, 양수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "4"
    )
    private int strokeCount;

    @NotBlank(message = "부수 입력은 필수입니다")
    @Schema(
            description = "한자의 부수 문자 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "日"
    )
    private String radical;

    @Positive(message = "레벨은 0보다 커야 합니다.")
    @Schema(
            description = "한자의 난이도 또는 학습 주기 레벨 (필수, 양수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3"
    )
    private int level;

    public Kanji toEntity() {
        return Kanji.builder()
                .character(this.character)
                .meaning(this.meaning)
                .meaningEn(this.meaningEn)
                .onyomi(this.onyomi)
                .kunyomi(this.kunyomi)
                .strokeCount(this.strokeCount)
                .radical(this.radical)
                .level(this.level)
                .build();
    }
}