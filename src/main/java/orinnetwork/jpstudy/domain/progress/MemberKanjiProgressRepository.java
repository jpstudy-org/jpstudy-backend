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

    @Query("SELECT DISTINCT p FROM MemberKanjiProgress p " +
            "JOIN FETCH p.kanji k " +
            "LEFT JOIN FETCH k.words " +
            "WHERE p.member = :member " +
            "AND p.nextReviewAt <= :now " +
            "AND p.masteryLevel != 'MASTERED'")
    List<MemberKanjiProgress> findDueForReview(@Param("member") Member member, @Param("now") LocalDateTime now);
}
