package orinnetwork.jpstudy.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordKanjiRepository extends JpaRepository<WordKanji, Long> {
    void deleteAllByWord(Word word);
}
