package orinnetwork.jpstudy.domain.progress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.member.Member;

public interface MemberKanjiProgressRepository extends JpaRepository<MemberKanjiProgress, Long> {

    Optional<MemberKanjiProgress> findByMemberAndKanji(Member member, Kanji kanji);

    @Query("SELECT p FROM MemberKanjiProgress p " +
            "JOIN FETCH p.kanji k " +
            "WHERE p.member = :member " +
            "AND p.nextReviewAt <= :now " +
            "AND p.masteryLevel != 'MASTERED'")
    List<MemberKanjiProgress> findDueForReview(@Param("member") Member member, @Param("now") LocalDateTime now);

    @Query("SELECT p FROM MemberKanjiProgress p JOIN FETCH p.kanji k " +
            "WHERE p.member = :member AND p.stability = 0.0 AND k.deletedAt IS NULL")
    List<MemberKanjiProgress> findUnreviewedByMember(@Param("member") Member member);

    @Query("SELECT p.masteryLevel, COUNT(p) FROM MemberKanjiProgress p " +
            "WHERE p.member = :member GROUP BY p.masteryLevel")
    List<Object[]> countByMasteryLevel(@Param("member") Member member);

    @Query("SELECT p.nextReviewAt FROM MemberKanjiProgress p " +
            "WHERE p.member = :member AND p.masteryLevel != 'MASTERED' AND p.stability > 0")
    List<LocalDateTime> findUpcomingReviewDates(@Param("member") Member member);
}
