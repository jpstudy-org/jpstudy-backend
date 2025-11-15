package orinnetwork.jpstudy.presentation.search.word;

import com.azure.core.annotation.Get;
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
import orinnetwork.jpstudy.application.search.word.WordSearchService;
import orinnetwork.jpstudy.application.search.word.dto.WordSearchResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/word")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    @GetMapping
    public ResponseEntity<CustomPageResponse<WordSearchResponse>> searchWords(
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 20) Pageable pageable
            ) {
        String lang = customUserDetails.getLanguage();

        CustomPageResponse<WordSearchResponse> results = wordSearchService.searchWords(keyword, lang, pageable);

        return ResponseEntity.ok(results);
    }
}
