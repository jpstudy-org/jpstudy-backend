package orinnetwork.jpstudy.domain.exam;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;

public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {

    Optional<MemberAnswer> findByTestAttemptAndQuestion(TestAttempt testAttempt, Question question);

    List<MemberAnswer> findByTestAttempt(TestAttempt testAttempt);
}
