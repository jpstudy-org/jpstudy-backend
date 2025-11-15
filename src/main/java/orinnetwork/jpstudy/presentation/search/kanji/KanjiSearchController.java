package orinnetwork.jpstudy.presentation.search.kanji;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/kanji")
public class KanjiSearchController {

    private final KanjiSearchService kanjiSearchService;

    @GetMapping
    public ResponseEntity<CustomPageResponse<KanjiSearchResponse>> searchKanjis(
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20) Pageable pageable
            ) {

        String lang = customUserDetails.getLanguage();
        CustomPageResponse<KanjiSearchResponse> results = kanjiSearchService.searchKanjis(keyword, lang, pageable);

        return ResponseEntity.ok(results);
    }
}
