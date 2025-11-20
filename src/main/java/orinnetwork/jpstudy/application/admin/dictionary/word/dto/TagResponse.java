package orinnetwork.jpstudy.application.admin.dictionary.word.dto;

import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.word.Tag;

@Getter
public class TagResponse {
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
