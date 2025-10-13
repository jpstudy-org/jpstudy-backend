package orinnetwork.jpstudy.domain.questionbank;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.level.id = :levelId AND q.category.name = :categoryName ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByLevelAndCategory(Long levelId, String categoryName, int limit);
}
