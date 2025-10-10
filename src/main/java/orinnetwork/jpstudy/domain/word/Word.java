package orinnetwork.jpstudy.domain.word;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.kanji.Kanji;

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

    // 뜻 (한국어)
    @Column(nullable = false)
    private String meaning;

    // 뜻 (영어)
    private String meaningEn;

    // 한자 여러 단어 포함 가능
    @ManyToMany
    private List<Kanji> kanjis = new ArrayList<>();

    @Builder
    public Word(String term, String reading, String meaning, String meaningEn, List<Kanji> kanjis) {
        this.term = term;
        this.reading = reading;
        this.meaning = meaning;
        this.meaningEn = meaningEn;
        this.kanjis = kanjis;
    }
}