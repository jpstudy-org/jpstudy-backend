package orinnetwork.jpstudy.application.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.post.Post;

@Getter
@Schema(description = "게시글 단건 조회 시 사용되는 상세 응답 DTO")
public class PostDetailResponse {

    @Schema(description = "게시글의 고유 ID")
    private final Long postId;

    @Schema(description = "게시글 제목")
    private final String title;

    @Schema(description = "게시글 본문 내용")
    private final String content;

    @Schema(description = "작성자 닉네임")
    private final String authorName;

    @Schema(description = "게시글 작성 시간")
    private final LocalDateTime createdAt;

    @Builder
    public PostDetailResponse(Long postId, String title, String content, String authorName, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getMember().getUsername())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
