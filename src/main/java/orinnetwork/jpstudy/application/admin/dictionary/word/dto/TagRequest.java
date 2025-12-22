package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "사전 단어 태그 생성/연결 요청 DTO")
public class TagRequest {

    @NotBlank(message = "태그 이름은 필수 입력 항목입니다.")
    @Schema(
            description = "태그의 이름 또는 내용 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "명사"
    )
    private String tag;
}