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
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlueprintDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_blueprint_id")
    private ExamBlueprint blueprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    private int questionCount;

    private int sequence;

    @Builder
    public BlueprintDetail(QuestionCategory category, int questionCount, int sequence) {
        this.category = category;
        this.questionCount = questionCount;
        this.sequence = sequence;
    }

    public void setBlueprint(ExamBlueprint blueprint) {
        this.blueprint = blueprint;
    }
}
