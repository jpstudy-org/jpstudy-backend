package orinnetwork.jpstudy.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordTagRepository extends JpaRepository<WordTag, Long> {
    void deleteAllByWord(Word word);
}
