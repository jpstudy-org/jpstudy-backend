package orinnetwork.jpstudy.domain.questionbank;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String passage;

    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private boolean isAIGenerated;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();

    @Builder
    public Question(Level level, QuestionCategory category, String questionText, String passage, String audioUrl, String explanation, boolean isAIGenerated, List<Choice> choices) {
        this.level = level;
        this.category = category;
        this.questionText = questionText;
        this.passage = passage;
        this.audioUrl = audioUrl;
        this.explanation = explanation;
        this.isAIGenerated = isAIGenerated;
        this.choices = (choices == null) ? new ArrayList<>() : choices;
    }

    public void update(Level level, QuestionCategory category, String questionText, String passage, String audioUrl, String explanation) {
        this.level = level;
        this.category = category;
        this.questionText = questionText;
        this.passage = passage;
        this.audioUrl = audioUrl;
        this.explanation = explanation;
    }
}
