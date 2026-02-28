package orinnetwork.jpstudy.presentation.anime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.anime.AnimeService;
import orinnetwork.jpstudy.application.anime.dto.AnimeResponse;
import orinnetwork.jpstudy.application.anime.dto.AnimeSearchRequest;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;

@Tag(name = "Anime API", description = "애니메이션 추천 API")
@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @Operation(summary = "추천 애니메이션 조회", description = "메인 페이지 상단에 노출되는 추천 애니메이션 목록 (2-3개)")
    @GetMapping("/featured")
    public ResponseEntity<List<AnimeResponse>> getFeatured() {
        return ResponseEntity.ok(animeService.getFeatured());
    }

    @Operation(summary = "인기 애니메이션 조회", description = "현재 인기 순위 기준 애니메이션 목록 (최대 25개)")
    @GetMapping("/trending")
    public ResponseEntity<List<AnimeResponse>> getTrending() {
        return ResponseEntity.ok(animeService.getTrending());
    }

    @Operation(summary = "시즌 애니메이션 조회", description = "현재 시즌 방영 중인 애니메이션 목록")
    @GetMapping("/seasonal")
    public ResponseEntity<List<AnimeResponse>> getSeasonal() {
        return ResponseEntity.ok(animeService.getSeasonal());
    }

    @Operation(summary = "애니메이션 검색", description = "제목, 장르, 연도, 상태 등으로 애니메이션 검색")
    @GetMapping("/search")
    public ResponseEntity<CustomPageResponse<AnimeResponse>> search(
            @Parameter(description = "검색어") @RequestParam(required = false) String q,
            @Parameter(description = "장르 필터") @RequestParam(required = false) List<String> genres,
            @Parameter(description = "시작 연도") @RequestParam(required = false) Integer yearFrom,
            @Parameter(description = "끝 연도") @RequestParam(required = false) Integer yearTo,
            @Parameter(description = "상태") @RequestParam(required = false) String status,
            @Parameter(description = "타입") @RequestParam(required = false) String type,
            @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
            @Parameter(description = "정렬 방향") @RequestParam(required = false) String order,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        AnimeSearchRequest request = new AnimeSearchRequest(q, genres, yearFrom, yearTo, status, type, sort, order);
        return ResponseEntity.ok(animeService.search(request, pageable));
    }

    @Operation(summary = "애니메이션 상세 조회", description = "ID로 애니메이션 상세 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<AnimeResponse> getById(
            @Parameter(description = "애니메이션 ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(animeService.getById(id));
    }

    @Operation(summary = "유사 애니메이션 조회", description = "해당 애니메이션과 유사한 추천 목록 (최대 10개)")
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<AnimeResponse>> getSimilar(
            @Parameter(description = "애니메이션 ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(animeService.getSimilar(id));
    }
}
