package orinnetwork.jpstudy.application.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import orinnetwork.jpstudy.domain.comment.Comment;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final String content;
    private final String authorName;
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
