package orinnetwork.jpstudy.application.anime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.anime.dto.AnimePageResponse;
import orinnetwork.jpstudy.application.anime.dto.AnimeResponse;
import orinnetwork.jpstudy.application.anime.dto.AnimeSearchRequest;
import orinnetwork.jpstudy.domain.anime.Anime;
import orinnetwork.jpstudy.domain.anime.AnimeList;
import orinnetwork.jpstudy.domain.anime.AnimeListRepository;
import orinnetwork.jpstudy.domain.anime.AnimeListType;
import orinnetwork.jpstudy.domain.anime.AnimeRepository;
import orinnetwork.jpstudy.domain.anime.AnimeStatus;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.jikan.JikanApiClient;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanAnimeData;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanAnimeDetailResponse;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanAnimeListResponse;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanRecommendationResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final AnimeListRepository animeListRepository;
    private final JikanApiClient jikanApiClient;

    public List<AnimeResponse> getFeatured() {
        return animeListRepository.findByListType(AnimeListType.FEATURED)
                .stream()
                .map(al -> AnimeResponse.from(al.getAnime()))
                .toList();
    }

    public List<AnimeResponse> getTrending() {
        return animeListRepository.findByListType(AnimeListType.TRENDING)
                .stream()
                .map(al -> AnimeResponse.from(al.getAnime()))
                .toList();
    }

    public List<AnimeResponse> getSeasonal() {
        return animeListRepository.findByListType(AnimeListType.SEASONAL)
                .stream()
                .map(al -> AnimeResponse.from(al.getAnime()))
                .toList();
    }

    public AnimePageResponse search(AnimeSearchRequest request, Pageable pageable) {
        Sort sort = resolveSort(request.sort());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        AnimeStatus status = parseStatus(request.status());

        // Parse comma-separated genres from frontend
        List<String> genres = parseGenres(request.genres());

        Page<Anime> page;
        if (genres != null && !genres.isEmpty()) {
            page = animeRepository.searchWithGenres(
                    request.q(),
                    status,
                    request.yearFrom(),
                    request.yearTo(),
                    request.type(),
                    genres,
                    sortedPageable
            );
        } else {
            page = animeRepository.search(
                    request.q(),
                    status,
                    request.yearFrom(),
                    request.yearTo(),
                    request.type(),
                    sortedPageable
            );
        }

        Page<AnimeResponse> responsePage = page.map(AnimeResponse::from);
        return AnimePageResponse.from(responsePage);
    }

    public AnimeResponse getById(Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ANIME_NOT_FOUND));

        if (anime.isStale()) {
            refreshAnimeAsync(anime.getId(), anime.getMalId());
        }

        return AnimeResponse.from(anime);
    }

    public List<AnimeResponse> getSimilar(Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ANIME_NOT_FOUND));

        List<JikanAnimeData> recommendations;
        try {
            JikanRecommendationResponse response = jikanApiClient.getAnimeRecommendations(anime.getMalId());
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return List.of();
            }
            recommendations = response.data().stream()
                    .limit(10)
                    .filter(entry -> entry.entry() != null && entry.entry().malId() != null)
                    .map(JikanRecommendationResponse.RecommendationEntry::entry)
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to fetch similar anime for id={}: {}", id, e.getMessage());
            return List.of();
        }

        return saveSimilarAnime(recommendations);
    }

    @Transactional
    public List<AnimeResponse> saveSimilarAnime(List<JikanAnimeData> recommendations) {
        List<AnimeResponse> results = new ArrayList<>();
        for (JikanAnimeData data : recommendations) {
            Anime similar = animeRepository.findByMalId(data.malId())
                    .orElseGet(() -> saveAnimeFromData(data));
            results.add(AnimeResponse.from(similar));
        }
        return results;
    }

    @Transactional
    public void refreshAnime(Long id, Integer malId) {
        try {
            JikanAnimeDetailResponse response = jikanApiClient.getAnimeById(malId);
            if (response != null && response.data() != null) {
                Anime anime = animeRepository.findById(id).orElse(null);
                if (anime != null) {
                    updateAnimeFromData(anime, response.data());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to refresh stale anime data for malId={}: {}", malId, e.getMessage());
        }
    }

    @Transactional
    public void syncTrending() {
        log.info("Starting trending anime sync...");
        try {
            JikanAnimeListResponse response = jikanApiClient.getTopAnime(1, 25);
            if (response == null || response.data() == null) return;

            syncList(AnimeListType.TRENDING, response.data());

            // Use top 3 as featured
            if (response.data().size() >= 3) {
                syncList(AnimeListType.FEATURED, response.data().subList(0, 3));
            }
            log.info("Trending anime sync completed. {} entries.", response.data().size());
        } catch (Exception e) {
            log.error("Failed to sync trending anime: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void syncSeasonal() {
        log.info("Starting seasonal anime sync...");
        try {
            JikanAnimeListResponse response = jikanApiClient.getCurrentSeasonAnime(1, 25);
            if (response == null || response.data() == null) return;

            syncList(AnimeListType.SEASONAL, response.data());
            log.info("Seasonal anime sync completed. {} entries.", response.data().size());
        } catch (Exception e) {
            log.error("Failed to sync seasonal anime: {}", e.getMessage(), e);
        }
    }

    private void refreshAnimeAsync(Long id, Integer malId) {
        // Fire-and-forget refresh in a separate thread to avoid holding the read transaction
        Thread.startVirtualThread(() -> {
            try {
                refreshAnime(id, malId);
            } catch (Exception e) {
                log.warn("Async refresh failed for malId={}: {}", malId, e.getMessage());
            }
        });
    }

    private void syncList(AnimeListType listType, List<JikanAnimeData> dataList) {
        animeListRepository.deleteByListType(listType);
        animeListRepository.flush();

        for (int i = 0; i < dataList.size(); i++) {
            JikanAnimeData data = dataList.get(i);
            Anime anime = animeRepository.findByMalId(data.malId())
                    .map(existing -> {
                        updateAnimeFromData(existing, data);
                        return existing;
                    })
                    .orElseGet(() -> saveAnimeFromData(data));

            animeListRepository.save(AnimeList.create(anime, listType, i));
        }
    }

    private Anime saveAnimeFromData(JikanAnimeData data) {
        LocalDate airedFrom = parseDate(data.aired() != null ? data.aired().from() : null);
        LocalDate airedTo = parseDate(data.aired() != null ? data.aired().to() : null);

        Anime anime = Anime.create(
                data.malId(),
                data.title(),
                data.titleJapanese(),
                data.getImageUrl(),
                data.synopsis(),
                data.type(),
                data.episodes(),
                AnimeStatus.fromJikan(data.status()),
                data.score(),
                data.scoredBy(),
                data.rank(),
                data.popularity(),
                data.members(),
                data.year(),
                data.season(),
                data.rating(),
                data.source(),
                data.duration(),
                data.getStudioName(),
                airedFrom,
                airedTo,
                data.getTrailerUrl(),
                data.getGenreNames()
        );
        return animeRepository.save(anime);
    }

    private void updateAnimeFromData(Anime anime, JikanAnimeData data) {
        LocalDate airedFrom = parseDate(data.aired() != null ? data.aired().from() : null);
        LocalDate airedTo = parseDate(data.aired() != null ? data.aired().to() : null);

        anime.updateFromApi(
                data.title(),
                data.titleJapanese(),
                data.getImageUrl(),
                data.synopsis(),
                data.type(),
                data.episodes(),
                AnimeStatus.fromJikan(data.status()),
                data.score(),
                data.scoredBy(),
                data.rank(),
                data.popularity(),
                data.members(),
                data.year(),
                data.season(),
                data.rating(),
                data.source(),
                data.duration(),
                data.getStudioName(),
                airedFrom,
                airedTo,
                data.getTrailerUrl(),
                data.getGenreNames()
        );
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    private AnimeStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return AnimeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<String> parseGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) return null;
        // Frontend sends genres as comma-separated single value: "Action,Fantasy"
        // Spring may bind it as a single-element list ["Action,Fantasy"]
        List<String> result = new ArrayList<>();
        for (String genre : genres) {
            if (genre.contains(",")) {
                result.addAll(Arrays.asList(genre.split(",")));
            } else {
                result.add(genre);
            }
        }
        return result.stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "score");
        }

        return switch (sort.toLowerCase()) {
            case "rating" -> Sort.by(Sort.Direction.DESC, "score");
            case "popularity" -> Sort.by(Sort.Direction.ASC, "popularity");
            case "newest" -> Sort.by(Sort.Direction.DESC, "year");
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.DESC, "score");
        };
    }
}
