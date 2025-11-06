package orinnetwork.jpstudy.domain.progress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.word.Word;

public interface MemberWordProgressRepository extends JpaRepository<MemberWordProgress, Long> {

    Optional<MemberWordProgress> findByMemberAndWord(Member member, Word word);

    @Query("SELECT p FROM MemberWordProgress p "
            + "JOIN FETCH p.word k "
            + "WHERE p.member = :member "
            + "AND p.nextReviewAt <= :now "
            + "AND p.masteryLevel != 'MASTERED'")
    List<MemberWordProgress> findDueForReview(@Param("member") Member member, @Param("now") LocalDateTime now);
}