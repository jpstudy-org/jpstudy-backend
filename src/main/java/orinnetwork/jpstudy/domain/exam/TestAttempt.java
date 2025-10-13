package orinnetwork.jpstudy.domain.exam;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer score;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    @Builder
    public TestAttempt(Member member, Exam exam) {
        this.member = member;
        this.exam = exam;
        this.startTime = LocalDateTime.now();
        this.status = AttemptStatus.IN_PROGRESS;
    }

    public enum AttemptStatus {
        IN_PROGRESS, COMPLETED
    }
}
