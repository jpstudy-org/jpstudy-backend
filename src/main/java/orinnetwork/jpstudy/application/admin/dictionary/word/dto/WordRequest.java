package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사전 단어 생성 및 수정 요청 DTO")
public class WordRequest {

    @NotBlank(message = "단어 입력은 필수입니다")
    @Schema(
            description = "단어의 표기 (한자 포함) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "日本語"
    )
    private String term;

    @NotBlank(message = "읽는 법 입력은 필수입니다")
    @Schema(
            description = "단어의 읽는 법 (히라가나/가타카나) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "にほんご"
    )
    private String reading;

    @Schema(description = "단어의 의미 및 예문 목록 (선택 사항)", nullable = true)
    private List<MeaningRequest> meanings;

    @Schema(description = "단어에 적용할 태그 목록 (선택 사항)", nullable = true)
    private List<TagRequest> tags;

    @NotNull(message = "레벨 입력은 필수입니다.")
    @Schema(
            description = "단어의 난이도 레벨 (예: JLPT N3 -> 3) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "3"
    )
    private int level;

    @Schema(
            description = "이 단어를 구성하는 한자 문자열 리스트 (예: '日本語' -> ['日', '本', '語']) (선택 사항)",
            nullable = true,
            example = "[\"日\", \"本\", \"語\"]"
    )
    private List<String> kanjiCharacters;
}