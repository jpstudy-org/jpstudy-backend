package orinnetwork.jpstudy.domain.anime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "anime_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnimeList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", nullable = false, length = 20)
    private AnimeListType listType;

    @Column(name = "rank_order", nullable = false)
    private Integer rankOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private AnimeList(Anime anime, AnimeListType listType, Integer rankOrder) {
        this.anime = anime;
        this.listType = listType;
        this.rankOrder = rankOrder;
        this.createdAt = LocalDateTime.now();
    }

    public static AnimeList create(Anime anime, AnimeListType listType, Integer rankOrder) {
        return AnimeList.builder()
                .anime(anime)
                .listType(listType)
                .rankOrder(rankOrder)
                .build();
    }
}
