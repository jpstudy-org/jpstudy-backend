package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Tag;

@Getter
@Schema(description = "사전 단어에 사용되는 태그 정보 응답 DTO")
public class TagResponse {

    @Schema(description = "태그의 이름 또는 내용", example = "동사")
    private final String tag;

    @Builder
    public TagResponse(String tag) {
        this.tag = tag;
    }

    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                .tag(tag.getName())
                .build();
    }
}
