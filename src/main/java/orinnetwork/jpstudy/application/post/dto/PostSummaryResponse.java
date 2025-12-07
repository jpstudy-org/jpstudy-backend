package orinnetwork.jpstudy.application.post.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.post.Post;

@Getter
public class PostSummaryResponse {

    private final Long postId;
    private final String title;
    private final String authorNickname;
    private final LocalDateTime createdAt;
    private final int viewCount;
    private final int commentCount;

    @Builder
    public PostSummaryResponse(Long postId, String title, String authorNickname, LocalDateTime createdAt, int viewCount,
                               int commentCount) {
        this.postId = postId;
        this.title = title;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public static PostSummaryResponse from(Post post, int viewCount) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .authorNickname(post.getMember().getUsername())
                .createdAt(post.getCreatedAt())
                .viewCount(viewCount)
                .commentCount(post.getCommentCount())
                .build();
    }
}
