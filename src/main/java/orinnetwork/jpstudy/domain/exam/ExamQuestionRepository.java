package orinnetwork.jpstudy.domain.exam;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
    List<ExamQuestion> findByExamIdOrderByQuestionNumberAsc(Long examId);
}
