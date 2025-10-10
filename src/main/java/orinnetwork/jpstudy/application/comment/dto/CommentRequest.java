package orinnetwork.jpstudy.application.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    private String content;

    @Builder
    public CommentRequest(String content) {
        this.content = content;
    }
}
