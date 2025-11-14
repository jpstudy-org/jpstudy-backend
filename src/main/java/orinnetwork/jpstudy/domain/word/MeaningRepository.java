package orinnetwork.jpstudy.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {

    void deleteAllByWord(Word word);
}
