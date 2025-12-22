package orinnetwork.jpstudy.presentation.search.word;

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
import orinnetwork.jpstudy.application.search.word.WordSearchService;
import orinnetwork.jpstudy.application.search.word.dto.WordSearchResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Tag(name = "Word Search API", description = "단어 검색 관련 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/word")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    @Operation(summary = "단어 검색", description = "키워드를 통해 단어를 검색")
    @GetMapping
    public ResponseEntity<CustomPageResponse<WordSearchResponse>> searchWords(
            @Parameter(description = "검색할 키워드 (Ex. 火)")
            @RequestParam(required = false) String keyword,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        String lang = customUserDetails.getLanguage();

        CustomPageResponse<WordSearchResponse> results = wordSearchService.searchWords(keyword, lang, pageable);

        return ResponseEntity.ok(results);
    }
}
