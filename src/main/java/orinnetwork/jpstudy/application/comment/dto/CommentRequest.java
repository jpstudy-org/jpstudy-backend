package orinnetwork.jpstudy.application.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "댓글 작성 및 수정 요청 DTO")
public class CommentRequest {

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    @Schema(
            description = "댓글 내용 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "좋은 정보 감사합니다!"
    )
    private String content;

    @Builder
    public CommentRequest(String content) {
        this.content = content;
    }
}
