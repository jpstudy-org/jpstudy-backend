package orinnetwork.jpstudy.domain.kanji;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    boolean existsByCharacterAndDeletedAtIsNull(String character);

    Optional<Kanji> findByCharacter(String character);

    Optional<Kanji> findByCharacterAndDeletedAtIsNull(String character);

    @Query("SELECT k FROM Kanji k WHERE k.deletedAt IS NULL AND ("
            + "k.character LIKE %:keyword% OR "
            + "k.meaning LIKE %:keyword% OR "
            + "k.onyomi LIKE %:keyword% OR "
            + "k.kunyomi LIKE %:keyword%)")
    Page<Kanji> searchActiveByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT k FROM Kanji k " +
            "WHERE k.deletedAt IS NULL AND k.level > 0 AND NOT EXISTS (" +
            "  SELECT 1 FROM MemberKanjiProgress p " +
            "  WHERE p.kanji = k AND p.member.id = :memberId" +
            ") ORDER BY k.level DESC, k.id ASC")
    List<Kanji> findNewKanjiForMember(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT k FROM Kanji k WHERE (" // deletedAt IS NULL 조건 제거
            + "k.character LIKE %:keyword% OR "
            + "k.meaning LIKE %:keyword% OR "
            + "k.meaningEn LIKE %:keyword% OR "
            + "k.onyomi LIKE %:keyword% OR "
            + "k.kunyomi LIKE %:keyword%)")
    Page<Kanji> searchAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Kanji> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT COUNT(k) FROM Kanji k " +
            "WHERE k.deletedAt IS NULL AND k.level > 0 AND NOT EXISTS (" +
            "  SELECT 1 FROM MemberKanjiProgress p " +
            "  WHERE p.kanji = k AND p.member.id = :memberId" +
            ")")
    long countNewKanjiForMember(@Param("memberId") Long memberId);
}