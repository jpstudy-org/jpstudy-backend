package orinnetwork.jpstudy.application.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 작성 및 수정 요청 DTO")
public class PostRequest {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다.")
    @Schema(
            description = "게시글 제목 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50,
            example = "최신 일본어 시험 후기입니다."
    )
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Schema(
            description = "게시글 내용 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "이번 시험은 청해가 특히 어려웠습니다..."
    )
    private String content;

    @Schema(description = "게시글이 속할 카테고리 ID (선택 사항)", nullable = true, example = "1")
    private Long categoryId;

    @Builder
    public PostRequest(String title, String content, Long categoryId) {
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
    }
}