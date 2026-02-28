package orinnetwork.jpstudy.application.anime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.anime.Anime;

@Schema(description = "애니메이션 응답 DTO")
public record AnimeResponse(
        @Schema(description = "DB ID") Long id,
        @Schema(description = "MAL ID") Integer malId,
        @Schema(description = "영어 제목") String title,
        @Schema(description = "일본어 제목") String titleJapanese,
        @Schema(description = "이미지 URL") String imageUrl,
        @Schema(description = "시놉시스") String synopsis,
        @Schema(description = "타입 (TV, Movie, OVA 등)") String type,
        @Schema(description = "에피소드 수") Integer episodes,
        @Schema(description = "상태") String status,
        @Schema(description = "평점") Double score,
        @Schema(description = "평점 참여자 수") Integer scoredBy,
        @Schema(description = "순위") Integer ranking,
        @Schema(description = "인기도") Integer popularity,
        @Schema(description = "멤버 수") Integer members,
        @Schema(description = "방영 연도") Integer year,
        @Schema(description = "시즌") String season,
        @Schema(description = "등급") String rating,
        @Schema(description = "원작") String source,
        @Schema(description = "에피소드 길이") String duration,
        @Schema(description = "트레일러 URL") String trailerUrl,
        @Schema(description = "장르 목록") List<String> genres
) {
    public static AnimeResponse from(Anime anime) {
        return new AnimeResponse(
                anime.getId(),
                anime.getMalId(),
                anime.getTitle(),
                anime.getTitleJapanese(),
                anime.getImageUrl(),
                anime.getSynopsis(),
                anime.getType(),
                anime.getEpisodes(),
                anime.getStatus() != null ? anime.getStatus().name() : null,
                anime.getScore(),
                anime.getScoredBy(),
                anime.getRanking(),
                anime.getPopularity(),
                anime.getMembers(),
                anime.getYear(),
                anime.getSeason(),
                anime.getRating(),
                anime.getSource(),
                anime.getDuration(),
                anime.getTrailerUrl(),
                anime.getGenres()
        );
    }
}
