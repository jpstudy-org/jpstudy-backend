package orinnetwork.jpstudy.domain.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.BaseEntity;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.post.Post;

@Entity
@Table(name = "comment", indexes = {
        @Index(name = "idx_comment_post_created", columnList = "post_id, created_at DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus commentStatus;

    @Column(length = 50)
    private String ipAddress;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(String content, Post post, Member member, String ipAddress, CommentStatus commentStatus) {
        this.content = content;
        this.post = post;
        this.member = member;
        this.ipAddress = ipAddress;
        this.commentStatus = commentStatus;
    }

    public static Comment write(String content, Post post, Member member, String ipAddress) {
        return Comment.builder()
                .content(content)
                .post(post)
                .member(member)
                .ipAddress(ipAddress)
                .commentStatus(CommentStatus.ACTIVE)
                .build();
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        if (this.commentStatus == CommentStatus.DELETED) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
        }
        this.commentStatus = CommentStatus.DELETED;
    }
}