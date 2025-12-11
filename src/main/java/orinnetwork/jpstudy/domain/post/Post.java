package orinnetwork.jpstudy.domain.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import orinnetwork.jpstudy.domain.BaseEntity;
import orinnetwork.jpstudy.domain.category.Category;
import orinnetwork.jpstudy.domain.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 50)
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus postStatus;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int viewCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int commentCount;

    @Builder(access = AccessLevel.PRIVATE)
    private Post(String title, String content, Member member, Category category, PostType postType,
                PostStatus postStatus, String ipAddress) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
        this.postType = postType;
        this.postStatus = postStatus;
        this.ipAddress = ipAddress;
    }

    public static Post create(String title, String content, Member member, Category category, String ipAddress) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .category(category)
                .postType(PostType.NORMAL)
                .postStatus(PostStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();
    }

    public static Post createNotice(String title, String content, Member member, String ipAddress) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .postType(PostType.NOTICE)
                .postStatus(PostStatus.ACTIVE)
                .ipAddress(ipAddress)
                .build();
    }

    public void changeStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public void update(String title, String content, Category category, PostStatus postStatus) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.postStatus = postStatus;
    }

    public void delete() {
        if (this.postStatus == PostStatus.DELETED) {
            throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
        }
        this.postStatus = PostStatus.DELETED;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}