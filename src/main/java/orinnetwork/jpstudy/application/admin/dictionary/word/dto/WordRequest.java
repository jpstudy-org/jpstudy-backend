package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WordRequest {

    @NotBlank(message = "단어 입력은 필수입니다")
    private String term;

    @NotBlank(message = "읽는 법 입력은 필수입니다")
    private String reading;

    private List<MeaningRequest> meanings;

    private List<TagRequest> tags;

    @NotNull(message = "레벨 입력은 필수입니다.")
    private int level;

    // 이 단어를 구성하는 한자 문자열 리스트
    // 예: "日本語" -> ["日", "本", "語"]
    private List<String> kanjiCharacters;
}