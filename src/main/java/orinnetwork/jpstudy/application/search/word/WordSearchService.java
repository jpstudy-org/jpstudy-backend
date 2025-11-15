package orinnetwork.jpstudy.application.search.word;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.search.word.dto.WordSearchResponse;
import orinnetwork.jpstudy.domain.word.Word;
import orinnetwork.jpstudy.domain.word.WordRepository;

@Service
@RequiredArgsConstructor
public class WordSearchService {

    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public CustomPageResponse<WordSearchResponse> searchWords(String keyword, String lang, Pageable pageable) {
        Page<Word> wordPage;

        if (keyword == null || keyword.isBlank()) {
            wordPage = wordRepository.findAllByDeletedAtIsNull(pageable);
        }
        else {
            wordPage = wordRepository.searchActiveByKeyword(keyword, pageable);
        }

        Page<WordSearchResponse> responses = wordPage.map(word -> new WordSearchResponse(word, lang));

        return new CustomPageResponse<>(responses);
    }
}
