package orinnetwork.jpstudy.domain.progress;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.word.Word;

@Table(
        name = "member_word_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_word",
                        columnNames = {"member_id", "word_id"}
                )
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWordProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningStatus status;

    @Column
    private LocalDateTime lastReviewedAt;

    @Column
    private LocalDateTime nextReviewAt;

    @Builder
    public MemberWordProgress(Member member, Word word) {
        this.member = member;
        this.word = word;
        this.status = LearningStatus.NOT_STARTED;
    }

    public void updateStatus(LearningStatus newStatus) {
        this.status = newStatus;
    }

    public void updateReviewSchedule(LocalDateTime lastReviewedAt, LocalDateTime nextReviewAt) {
        this.lastReviewedAt = lastReviewedAt;
        this.nextReviewAt = nextReviewAt;
    }
}
