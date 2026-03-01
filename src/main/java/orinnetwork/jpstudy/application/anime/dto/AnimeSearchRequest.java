package orinnetwork.jpstudy.application.anime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "애니메이션 검색 요청 DTO")
public record AnimeSearchRequest(
        @Schema(description = "검색어") String q,
        @Schema(description = "장르 필터") List<String> genres,
        @Schema(description = "시작 연도") Integer yearFrom,
        @Schema(description = "끝 연도") Integer yearTo,
        @Schema(description = "상태 (AIRING, FINISHED, UPCOMING)") String status,
        @Schema(description = "타입 (TV, Movie, OVA 등)") String type,
        @Schema(description = "정렬 기준 (score, popularity, title, year)") String sort,
        @Schema(description = "정렬 방향 (asc, desc)") String order
) {}
