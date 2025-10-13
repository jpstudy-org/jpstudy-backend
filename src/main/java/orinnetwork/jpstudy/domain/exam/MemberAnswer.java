package orinnetwork.jpstudy.domain.exam;

import jakarta.persistence.Entity;
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
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Question;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private TestAttempt testAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_choice_id")
    private Choice selectedChoice;

    private boolean isCorrect;

    @Builder
    public MemberAnswer(TestAttempt testAttempt, Question question, Choice selectedChoice, boolean isCorrect) {
        this.testAttempt = testAttempt;
        this.question = question;
        this.selectedChoice = selectedChoice;
        this.isCorrect = isCorrect;
    }
}
