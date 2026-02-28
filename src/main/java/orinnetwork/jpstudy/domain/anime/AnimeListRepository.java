package orinnetwork.jpstudy.domain.anime;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnimeListRepository extends JpaRepository<AnimeList, Long> {

    @Query("SELECT al FROM AnimeList al JOIN FETCH al.anime WHERE al.listType = :listType ORDER BY al.rankOrder ASC")
    List<AnimeList> findByListType(@Param("listType") AnimeListType listType);

    void deleteByListType(AnimeListType listType);
}
