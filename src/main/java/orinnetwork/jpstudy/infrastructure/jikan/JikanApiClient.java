package orinnetwork.jpstudy.infrastructure.jikan;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanAnimeDetailResponse;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanAnimeListResponse;
import orinnetwork.jpstudy.infrastructure.jikan.dto.JikanRecommendationResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JikanApiClient {

    private final RestClient jikanRestClient;

    private long lastRequestTime = 0;
    private static final long MIN_REQUEST_INTERVAL_MS = 340; // ~3 req/sec

    public JikanAnimeListResponse getTopAnime(int page, int limit) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/top/anime?page={page}&limit={limit}", page, limit)
                .retrieve()
                .body(JikanAnimeListResponse.class);
    }

    public JikanAnimeListResponse getSeasonalAnime(int year, String season, int page, int limit) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/seasons/{year}/{season}?page={page}&limit={limit}", year, season, page, limit)
                .retrieve()
                .body(JikanAnimeListResponse.class);
    }

    public JikanAnimeListResponse getCurrentSeasonAnime(int page, int limit) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/seasons/now?page={page}&limit={limit}", page, limit)
                .retrieve()
                .body(JikanAnimeListResponse.class);
    }

    public JikanAnimeDetailResponse getAnimeById(int malId) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/anime/{id}/full", malId)
                .retrieve()
                .body(JikanAnimeDetailResponse.class);
    }

    public JikanRecommendationResponse getAnimeRecommendations(int malId) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/anime/{id}/recommendations", malId)
                .retrieve()
                .body(JikanRecommendationResponse.class);
    }

    public JikanAnimeListResponse searchAnime(String query, int page, int limit) {
        rateLimit();
        return jikanRestClient.get()
                .uri("/anime?q={q}&page={page}&limit={limit}", query, page, limit)
                .retrieve()
                .body(JikanAnimeListResponse.class);
    }

    public String getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 1 && month <= 3) return "winter";
        if (month >= 4 && month <= 6) return "spring";
        if (month >= 7 && month <= 9) return "summer";
        return "fall";
    }

    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    private synchronized void rateLimit() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRequestTime;
        if (elapsed < MIN_REQUEST_INTERVAL_MS) {
            try {
                Thread.sleep(MIN_REQUEST_INTERVAL_MS - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }
}
