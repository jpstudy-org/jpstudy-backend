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
import orinnetwork.jpstudy.domain.questionbank.Level;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    private String title;

    private int totalTimeMinutes;

    @Builder(access = AccessLevel.PRIVATE)
    private Exam(Level level, String title, int totalTimeMinutes) {
        this.level = level;
        this.title = title;
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public static Exam create(Level level, String title, int totalTimeMinutes) {
        return Exam.builder()
                .level(level)
                .title(title)
                .totalTimeMinutes(totalTimeMinutes)
                .build();
    }
}
