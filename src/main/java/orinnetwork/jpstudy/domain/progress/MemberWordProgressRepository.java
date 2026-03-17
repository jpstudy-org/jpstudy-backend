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

    @Query("SELECT DISTINCT p FROM MemberWordProgress p "
            + "JOIN FETCH p.word w "
            + "LEFT JOIN FETCH w.meanings m "
            + "WHERE p.member = :member "
            + "AND p.nextReviewAt <= :now "
            + "AND p.masteryLevel != 'MASTERED'")
    List<MemberWordProgress> findDueForReview(@Param("member") Member member, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT p FROM MemberWordProgress p "
            + "JOIN FETCH p.word w "
            + "LEFT JOIN FETCH w.meanings m "
            + "WHERE p.member = :member AND p.stability = 0.0 AND w.deletedAt IS NULL")
    List<MemberWordProgress> findUnreviewedByMember(@Param("member") Member member);

    @Query("SELECT p.masteryLevel, COUNT(p) FROM MemberWordProgress p " +
            "WHERE p.member = :member GROUP BY p.masteryLevel")
    List<Object[]> countByMasteryLevel(@Param("member") Member member);

    @Query("SELECT p.nextReviewAt FROM MemberWordProgress p " +
            "WHERE p.member = :member AND p.masteryLevel != 'MASTERED' AND p.stability > 0")
    List<LocalDateTime> findUpcomingReviewDates(@Param("member") Member member);
}