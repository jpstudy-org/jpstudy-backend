package orinnetwork.jpstudy.domain.anime;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer malId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String titleJapanese;

    @Column(length = 1000)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(length = 20)
    private String type;

    private Integer episodes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnimeStatus status;

    private Double score;
    private Integer scoredBy;

    @Column(name = "anime_rank")
    private Integer animeRank;

    private Integer popularity;
    private Integer members;
    private Integer year;

    @Column(length = 20)
    private String season;

    @Column(name = "age_rating", length = 50)
    private String ageRating;

    @Column(length = 50)
    private String source;

    @Column(length = 50)
    private String duration;

    @Column(length = 200)
    private String studio;

    private LocalDate airedFrom;
    private LocalDate airedTo;

    @Column(length = 1000)
    private String trailerUrl;

    @Column(nullable = false)
    private LocalDateTime fetchedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "anime_genre", joinColumns = @JoinColumn(name = "anime_id"))
    @Column(name = "genre")
    @BatchSize(size = 25)
    private List<String> genres = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Anime(Integer malId, String title, String titleJapanese, String imageUrl,
                  String synopsis, String type, Integer episodes, AnimeStatus status,
                  Double score, Integer scoredBy, Integer animeRank, Integer popularity,
                  Integer members, Integer year, String season, String ageRating,
                  String source, String duration, String studio,
                  LocalDate airedFrom, LocalDate airedTo,
                  String trailerUrl, List<String> genres) {
        this.malId = malId;
        this.title = title;
        this.titleJapanese = titleJapanese;
        this.imageUrl = imageUrl;
        this.synopsis = synopsis;
        this.type = type;
        this.episodes = episodes;
        this.status = status;
        this.score = score;
        this.scoredBy = scoredBy;
        this.animeRank = animeRank;
        this.popularity = popularity;
        this.members = members;
        this.year = year;
        this.season = season;
        this.ageRating = ageRating;
        this.source = source;
        this.duration = duration;
        this.studio = studio;
        this.airedFrom = airedFrom;
        this.airedTo = airedTo;
        this.trailerUrl = trailerUrl;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.fetchedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Anime create(Integer malId, String title, String titleJapanese, String imageUrl,
                                String synopsis, String type, Integer episodes, AnimeStatus status,
                                Double score, Integer scoredBy, Integer animeRank, Integer popularity,
                                Integer members, Integer year, String season, String ageRating,
                                String source, String duration, String studio,
                                LocalDate airedFrom, LocalDate airedTo,
                                String trailerUrl, List<String> genres) {
        return Anime.builder()
                .malId(malId)
                .title(title)
                .titleJapanese(titleJapanese)
                .imageUrl(imageUrl)
                .synopsis(synopsis)
                .type(type)
                .episodes(episodes)
                .status(status)
                .score(score)
                .scoredBy(scoredBy)
                .animeRank(animeRank)
                .popularity(popularity)
                .members(members)
                .year(year)
                .season(season)
                .ageRating(ageRating)
                .source(source)
                .duration(duration)
                .studio(studio)
                .airedFrom(airedFrom)
                .airedTo(airedTo)
                .trailerUrl(trailerUrl)
                .genres(genres)
                .build();
    }

    public void updateFromApi(String title, String titleJapanese, String imageUrl,
                              String synopsis, String type, Integer episodes, AnimeStatus status,
                              Double score, Integer scoredBy, Integer animeRank, Integer popularity,
                              Integer members, Integer year, String season, String ageRating,
                              String source, String duration, String studio,
                              LocalDate airedFrom, LocalDate airedTo,
                              String trailerUrl, List<String> genres) {
        this.title = title;
        this.titleJapanese = titleJapanese;
        this.imageUrl = imageUrl;
        this.synopsis = synopsis;
        this.type = type;
        this.episodes = episodes;
        this.status = status;
        this.score = score;
        this.scoredBy = scoredBy;
        this.animeRank = animeRank;
        this.popularity = popularity;
        this.members = members;
        this.year = year;
        this.season = season;
        this.ageRating = ageRating;
        this.source = source;
        this.duration = duration;
        this.studio = studio;
        this.airedFrom = airedFrom;
        this.airedTo = airedTo;
        this.trailerUrl = trailerUrl;
        this.genres.clear();
        if (genres != null) {
            this.genres.addAll(genres);
        }
        this.fetchedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isStale() {
        return fetchedAt.isBefore(LocalDateTime.now().minusHours(24));
    }
}
