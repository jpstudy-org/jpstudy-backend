package orinnetwork.jpstudy.domain.word;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.domain.kanji.Kanji;

@Entity
@Getter
@Table(name = "word_kanji")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordKanji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanji_id")
    private Kanji kanji;

    public WordKanji(Word word, Kanji kanji) {
        this.word = word;
        this.kanji = kanji;
    }

    public void setWord(Word word) {
        this.word = word;
        word.getWordKanjis().add(this);
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
        kanji.getWordKanjis().add(this);
    }
}
