package orinnetwork.jpstudy.application.search.kanji;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.search.kanji.dto.KanjiSearchResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;

@Service
@RequiredArgsConstructor
public class KanjiSearchService {

    private final KanjiRepository kanjiRepository;

    /**
     * 사용자 한자 조회 기능
     *
     * @param keyword  키워드
     * @param lang     언어
     * @param pageable 페이징
     * @return 페이징
     */
    @Transactional(readOnly = true)
    public CustomPageResponse<KanjiSearchResponse> searchKanjis(String keyword, String lang, Pageable pageable) {
        Page<Kanji> kanjiPage;

        if (keyword == null || keyword.isBlank()) {
            kanjiPage = kanjiRepository.findAllByDeletedAtIsNull(pageable);
        } else {
            kanjiPage = kanjiRepository.searchActiveByKeyword(keyword, pageable);
        }

        Page<KanjiSearchResponse> responses = kanjiPage.map(kanji -> new KanjiSearchResponse(kanji, lang));
        return new CustomPageResponse<>(responses);
    }
}
