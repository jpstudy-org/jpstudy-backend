package orinnetwork.jpstudy.domain.exam;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import orinnetwork.jpstudy.domain.exam.TestAttempt.AttemptStatus;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    Optional<TestAttempt> findFirstByMemberIdAndStatusOrderByStartTimeDesc(Long memberId, AttemptStatus status);
}
