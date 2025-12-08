package orinnetwork.jpstudy.application.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.post.Post;

@Getter
@Schema(description = "게시글 목록 조회 시 사용되는 요약 응답 DTO")
public class PostSummaryResponse {

    @Schema(description = "게시글의 고유 ID")
    private final Long postId;

    @Schema(description = "게시글 제목")
    private final String title;

    @Schema(description = "작성자 닉네임")
    private final String authorNickname;

    @Schema(description = "게시글 작성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "조회수")
    private final int viewCount;

    @Schema(description = "댓글 개수")
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
