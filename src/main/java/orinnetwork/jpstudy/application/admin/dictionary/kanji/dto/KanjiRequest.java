package orinnetwork.jpstudy.application.admin.dictionary.kanji.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Getter
@NoArgsConstructor
public class KanjiRequest {

    @NotBlank(message = "한자 입력은 필수입니다")
    private String character;

    @NotBlank(message = "뜻 입력은 필수입니다")
    private String meaning;

    @NotBlank(message = "영문 뜻 입력은 필수입니다")
    private String meaningEn;

    @NotBlank(message = "음독 입력은 필수입니다")
    private String onyomi;

    @NotBlank(message = "훈독 입력은 필수입니다")
    private String kunyomi;

    @NotBlank(message = "획수 입력은 필수입니다")
    private int strokeCount;

    @NotBlank(message = "부수 입력은 필수입니다")
    private String radical;

    @NotBlank(message = "레벨 입력은 필수입니다. (학습 주기 레벨)")
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