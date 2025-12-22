package orinnetwork.jpstudy.application.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.comment.Comment;

@Getter
@Schema(description = "댓글 상세 정보 응답 DTO")
public class CommentResponse {

    @Schema(description = "댓글의 고유 ID")
    private final Long commentId;

    @Schema(description = "댓글 내용")
    private final String content;

    @Schema(description = "작성자 닉네임")
    private final String authorName;

    @Schema(description = "댓글 작성 시간")
    private final LocalDateTime createdAt;

    @Builder
    public CommentResponse(Long commentId, String content, String authorName, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getMember().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
