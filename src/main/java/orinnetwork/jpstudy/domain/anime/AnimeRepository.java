package orinnetwork.jpstudy.domain.anime;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnimeRepository extends JpaRepository<Anime, Long> {

    Optional<Anime> findByMalId(Integer malId);

    List<Anime> findByMalIdIn(List<Integer> malIds);

    @Query("""
            SELECT a FROM Anime a
            WHERE (:query IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(a.titleJapanese) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:status IS NULL OR a.status = :status)
            AND (:yearFrom IS NULL OR a.year >= :yearFrom)
            AND (:yearTo IS NULL OR a.year <= :yearTo)
            AND (:type IS NULL OR a.type = :type)
            """)
    Page<Anime> search(
            @Param("query") String query,
            @Param("status") AnimeStatus status,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            @Param("type") String type,
            Pageable pageable
    );

    @Query(value = """
            SELECT DISTINCT a.* FROM anime a
            INNER JOIN anime_genre ag ON a.id = ag.anime_id
            WHERE (:query IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(a.title_japanese) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:status IS NULL OR a.status = :status)
            AND (:yearFrom IS NULL OR a.year >= :yearFrom)
            AND (:yearTo IS NULL OR a.year <= :yearTo)
            AND (:type IS NULL OR a.type = :type)
            AND ag.genre IN (:genres)
            """,
            countQuery = """
            SELECT COUNT(DISTINCT a.id) FROM anime a
            INNER JOIN anime_genre ag ON a.id = ag.anime_id
            WHERE (:query IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(a.title_japanese) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:status IS NULL OR a.status = :status)
            AND (:yearFrom IS NULL OR a.year >= :yearFrom)
            AND (:yearTo IS NULL OR a.year <= :yearTo)
            AND (:type IS NULL OR a.type = :type)
            AND ag.genre IN (:genres)
            """,
            nativeQuery = true)
    Page<Anime> searchWithGenres(
            @Param("query") String query,
            @Param("status") String status,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            @Param("type") String type,
            @Param("genres") List<String> genres,
            Pageable pageable
    );
}
