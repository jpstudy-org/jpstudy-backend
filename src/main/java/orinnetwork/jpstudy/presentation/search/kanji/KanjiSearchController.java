package orinnetwork.jpstudy.presentation.search.kanji;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.search.kanji.KanjiSearchService;
import orinnetwork.jpstudy.application.search.kanji.dto.KanjiSearchResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Tag(name = "Search API", description = "검색 관련 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/kanji")
public class KanjiSearchController {

    private final KanjiSearchService kanjiSearchService;

    @Operation(summary = "한자 검색", description = "키워드를 통해 한자를 검색")
    @GetMapping
    public ResponseEntity<CustomPageResponse<KanjiSearchResponse>> searchKanjis(
            @Parameter(description = "검색할 키워드 (Ex. 火)")
            @RequestParam(required = false) String keyword,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {

        String lang = customUserDetails.getLanguage();
        CustomPageResponse<KanjiSearchResponse> results = kanjiSearchService.searchKanjis(keyword, lang, pageable);

        return ResponseEntity.ok(results);
    }
}
