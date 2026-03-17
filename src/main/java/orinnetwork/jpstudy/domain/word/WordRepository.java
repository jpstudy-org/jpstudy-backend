package orinnetwork.jpstudy.domain.word;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WordRepository extends JpaRepository<Word, Long> {

    // 중복 단어 검사
    boolean existsByTermAndDeletedAtIsNull(String term);

    // 삭제된 단어 포함해서 term으로 조회
    Optional<Word> findByTerm(String term);

    // 활성 단어 전체 페이징 조회
    Page<Word> findAllByDeletedAtIsNull(Pageable pageable);

    // 활성 단어 키워드 검색
    @Query("SELECT DISTINCT w FROM Word w " +
            "LEFT JOIN w.meanings m " +
            "WHERE w.deletedAt IS NULL AND (" +
            "w.term LIKE CONCAT('%', :keyword, '%') OR " +
            "w.reading LIKE CONCAT('%', :keyword, '%') OR " +
            "m.meaningKr LIKE CONCAT('%', :keyword, '%') OR " +
            "m.meaningEn LIKE CONCAT('%', :keyword, '%'))")
    Page<Word> searchActiveByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT w FROM Word w "
            + "WHERE w.deletedAt IS NULL AND NOT EXISTS ("
            + "SELECT 1 FROM MemberWordProgress p "
            + "WHERE p.word = w AND p.member.id = :memberId"
            + ") ORDER BY w.level DESC, w.id ASC",
            countQuery = "SELECT count(w) FROM Word w "
                    + "WHERE w.deletedAt IS NULL AND NOT EXISTS ("
                    + "SELECT 1 FROM MemberWordProgress p "
                    + "WHERE p.word = w AND p.member.id = :memberId"
                    + ")")
    List<Word> findNewWordForMember(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT COUNT(w) FROM Word w " +
            "WHERE w.deletedAt IS NULL AND NOT EXISTS (" +
            "  SELECT 1 FROM MemberWordProgress p " +
            "  WHERE p.word = w AND p.member.id = :memberId" +
            ")")
    long countNewWordForMember(@Param("memberId") Long memberId);
}