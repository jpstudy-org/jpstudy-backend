package orinnetwork.jpstudy.domain.exam;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.questionbank.Level;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamBlueprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    private int totalTimeMinutes;

    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<BlueprintDetail> details = new ArrayList<>();

    @Builder
    public ExamBlueprint(String title, String description, Level level, int totalTimeMinutes) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public void addDetail(BlueprintDetail details) {
        this.details.add(details);
        details.setBlueprint(this);
    }

    public void updateInfo(String title, String description, Level level, int totalTimeMinutes) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public void clearDetails() {
        this.details.clear();
    }
}
