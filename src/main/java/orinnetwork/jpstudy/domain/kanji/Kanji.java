package orinnetwork.jpstudy.domain.kanji;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.word.WordKanji;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한자
    @Column(nullable = false, unique = true, length = 10)
    private String character;

    // 뜻 (한국어)
    @Column(nullable = false)
    private String meaning;

    // 뜻 (영어)
    @Column
    private String meaningEn;

    // 음독
    @Column(nullable = false)
    private String onyomi;

    // 훈독
    @Column(nullable = false)
    private String kunyomi;

    // 획수
    @Column(nullable = false)
    private int strokeCount;

    // 부수
    @Column
    private String radical;

    // 급수
    @Column
    private Integer level;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "kanji")
    private List<WordKanji> wordKanjis = new ArrayList<>();

    @Builder
    public Kanji(String character, String meaning, String meaningEn, String onyomi, String kunyomi,
                 int strokeCount, String radical, Integer level) {
        this.character = character;
        this.meaning = meaning;
        this.meaningEn = meaningEn;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.strokeCount = strokeCount;
        this.radical = radical;
        this.level = level;
    }

    public void updateDetails(String meaning, String meaningEn, String onyomi, String kunyomi,
                              int strokeCount, String radical, Integer level) {
        this.meaning = meaning;
        this.meaningEn = meaningEn;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.strokeCount = strokeCount;
        this.radical = radical;
        this.level = level;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
