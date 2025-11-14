package orinnetwork.jpstudy.domain.word;

import jakarta.persistence.CascadeType;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단어
    @Column(nullable = false)
    private String term;

    // 읽는 법
    @Column(nullable = false)
    private String reading;

    @Column(nullable = false)
    private int level;

    // 한자 여러 단어 포함 가능
    @OneToMany(mappedBy = "word")
    private List<WordKanji> wordKanjis = new ArrayList<>();

    // 단어 뜻 (여러 개 일 수 있음)
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meaning> meanings = new ArrayList<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordTag> wordTags = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Word(String term, String reading, int level) {
        this.term = term;
        this.reading = reading;
        this.level = level;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public void updateDetails(String reading, int level) {
        this.reading = reading;
        this.level = level;
    }

    public void addMeaning(Meaning meaning) {
        this.meanings.add(meaning);
        meaning.setWord(this);
    }

    public void clearMeanings() {
        this.meanings.clear();
    }

    public void addWordTag(WordTag wordTag) {
        this.wordTags.add(wordTag);
    }

    public void clearWordTags() {
        this.wordTags.clear();
    }
}