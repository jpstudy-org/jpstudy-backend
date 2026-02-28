package orinnetwork.jpstudy.application.anime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.anime.Anime;

@Schema(description = "애니메이션 응답 DTO")
public record AnimeResponse(
        @Schema(description = "DB ID") Long id,
        @Schema(description = "영어 제목") String title,
        @Schema(description = "일본어 제목") String titleJp,
        @Schema(description = "영어 제목 (title과 동일)") String titleEn,
        @Schema(description = "시놉시스") String synopsis,
        @Schema(description = "포스터 이미지 URL") String posterUrl,
        @Schema(description = "배너 이미지 URL") String bannerUrl,
        @Schema(description = "장르 목록") List<String> genres,
        @Schema(description = "평점 (0-10)") Double rating,
        @Schema(description = "에피소드 수") Integer episodes,
        @Schema(description = "상태 (airing, finished, upcoming)") String status,
        @Schema(description = "시즌 (예: Winter 2026)") String season,
        @Schema(description = "스튜디오") String studio,
        @Schema(description = "방영 연도") Integer year,
        @Schema(description = "MAL URL") String malUrl,
        @Schema(description = "AniList URL") String anilistUrl
) {
    public static AnimeResponse from(Anime anime) {
        String statusStr = anime.getStatus() != null
                ? anime.getStatus().name().toLowerCase()
                : null;

        String seasonDisplay = formatSeason(anime.getSeason(), anime.getYear());

        String malUrl = anime.getMalId() != null
                ? "https://myanimelist.net/anime/" + anime.getMalId()
                : null;

        return new AnimeResponse(
                anime.getId(),
                anime.getTitle(),
                anime.getTitleJapanese(),
                anime.getTitle(),
                anime.getSynopsis(),
                anime.getImageUrl(),
                null,
                anime.getGenres(),
                anime.getScore(),
                anime.getEpisodes(),
                statusStr,
                seasonDisplay,
                anime.getStudio(),
                anime.getYear(),
                malUrl,
                null
        );
    }

    private static String formatSeason(String season, Integer year) {
        if (season == null || year == null) {
            return year != null ? String.valueOf(year) : null;
        }
        String capitalized = season.substring(0, 1).toUpperCase() + season.substring(1).toLowerCase();
        return capitalized + " " + year;
    }
}
