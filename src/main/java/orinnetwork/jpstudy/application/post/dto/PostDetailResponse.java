package orinnetwork.jpstudy.application.post.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.post.Post;

@Getter
public class PostDetailResponse {

    private final Long postId;
    private final String title;
    private final String content;
    private final String authorName;
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
