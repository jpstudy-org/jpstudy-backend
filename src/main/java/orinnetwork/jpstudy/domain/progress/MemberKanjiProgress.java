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

    @Column(nullable = false)
    private Double stability = 0.0;

    @Column(nullable = false)
    private Double difficulty = 0.0;

    public MemberKanjiProgress(Member member, Kanji kanji, LocalDateTime now) {
        this.member = member;
        this.kanji = kanji;
        this.lastReviewedAt = now;
        this.nextReviewAt = now;

        this.masteryLevel = MasteryLevel.NEW;
        this.stability = 0.0;
        this.difficulty = 0.0;
    }

    public void updateFsrs(Double newStability, Double newDifficulty, LocalDateTime lastReviewedAt,
                           LocalDateTime nextReviewAt) {
        this.stability = newStability;
        this.difficulty = newDifficulty;
        this.lastReviewedAt = lastReviewedAt;
        this.nextReviewAt = nextReviewAt;

        updateMasteryLevelByStability();
    }

    private void updateMasteryLevelByStability() {
        if (this.stability < 1.0) {
            this.masteryLevel = MasteryLevel.NEW;
        } else if (this.stability < 21.0) {
            this.masteryLevel = MasteryLevel.APPRENTICE;
        } else if (this.stability < 90.0) {
            this.masteryLevel = MasteryLevel.GURU;
        } else {
            this.masteryLevel = MasteryLevel.MASTERED;
        }
    }
}
