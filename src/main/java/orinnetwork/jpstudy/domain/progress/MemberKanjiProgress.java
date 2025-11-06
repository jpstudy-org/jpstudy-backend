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
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.member.Member;

@Table(
        name = "member_kanji_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "member_kanji_unique",
                        columnNames = {"member_id", "kanji_id"}
                )
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberKanjiProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanji_id", nullable = false)
    private Kanji kanji;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MasteryLevel masteryLevel;

    @Column
    private LocalDateTime lastReviewedAt;

    @Column
    private LocalDateTime nextReviewAt;

    public MemberKanjiProgress(Member member, Kanji kanji, MasteryLevel masteryLevel, LocalDateTime lastReviewedAt,
                               LocalDateTime nextReviewAt) {
        this.member = member;
        this.kanji = kanji;
        this.masteryLevel = masteryLevel;
        this.lastReviewedAt = lastReviewedAt;
        this.nextReviewAt = nextReviewAt;
    }

    public void update(MasteryLevel newMasteryLevel, LocalDateTime lastReviewedAt, LocalDateTime nextReviewAt) {
        this.masteryLevel = newMasteryLevel;
        this.lastReviewedAt = lastReviewedAt;
        this.nextReviewAt = nextReviewAt;
    }
}
