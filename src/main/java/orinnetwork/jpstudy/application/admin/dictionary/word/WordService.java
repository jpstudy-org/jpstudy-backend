package orinnetwork.jpstudy.application.admin.dictionary.word;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordRequest;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;
import orinnetwork.jpstudy.domain.word.Word;
import orinnetwork.jpstudy.domain.word.WordKanji;
import orinnetwork.jpstudy.domain.word.WordKanjiRepository;
import orinnetwork.jpstudy.domain.word.WordRepository;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final KanjiRepository kanjiRepository;
    private final WordKanjiRepository wordKanjiRepository;

    // 단어 생성 (단일)
    @Transactional
    public WordResponse createWord(WordRequest request) {
        validateDuplicateWord(request.getTerm());

        List<Kanji> foundKanjis = findAndValidateKanjis(request.getKanjiCharacters());

        Word newWord = Word.builder()
                .term(request.getTerm())
                .reading(request.getReading())
                .meaning(request.getMeaning())
                .meaningEn(request.getMeaningEn())
                .level(request.getLevel())
                .build();
        Word savedWord = wordRepository.save(newWord);

        linkKanjisToWord(savedWord, foundKanjis);

        return WordResponse.from(savedWord);
    }

    // 단어 생성 (다중)
    @Transactional
    public List<WordResponse> createOrUpdateWords(List<WordRequest> requests) {
        List<WordResponse> responses = new ArrayList<>();

        for (WordRequest req : requests) {
            Word word = wordRepository.findByTerm(req.getTerm())
                    .orElse(null);

            List<Kanji> foundKanji = findAndValidateKanjis(req.getKanjiCharacters());

            Word savedWord;
            if (word != null) {
                word.updateDetails(req.getReading(), req.getMeaning(), req.getMeaningEn(), req.getLevel());
                word.restore();

                clearKanjiLinks(word);
                savedWord = word;
            } else {
                validateDuplicateWord(req.getTerm());
                Word newWord = Word.builder()
                        .term(req.getTerm())
                        .reading(req.getReading())
                        .meaning(req.getMeaning())
                        .meaningEn(req.getMeaningEn())
                        .level(req.getLevel())
                        .build();

                savedWord = wordRepository.save(newWord);
            }

            linkKanjisToWord(savedWord, foundKanji);
            responses.add(WordResponse.from(savedWord));
        }
        return responses;
    }

    // 단어 삭제
    @Transactional
    public void deleteWord(WordRequest wordRequest) {
        Word word = wordRepository.findByTerm(wordRequest.getTerm())
                .orElseThrow(() -> new IllegalArgumentException("해당 단어는 존재하지 않습니다."));

        word.softDelete();
    }

    // 단어 수정
    @Transactional
    public WordResponse updateWord(WordRequest request) {

        Word word = wordRepository.findByTerm(request.getTerm())
                .orElseThrow(() -> new IllegalArgumentException("해당 단어는 존재하지 않습니다."));

        List<Kanji> foundKanjis = findAndValidateKanjis(request.getKanjiCharacters());

        word.updateDetails(request.getReading(), request.getMeaning(), request.getMeaningEn(), request.getLevel());

        clearKanjiLinks(word);
        linkKanjisToWord(word, foundKanjis);

        return WordResponse.from(word);
    }

    // 단어 조회
    @Transactional(readOnly = true)
    public CustomPageResponse<WordResponse> getWords(String keyword, Pageable pageable) {
        Page<Word> wordPage;

        if (keyword == null || keyword.isBlank()) {
            wordPage = wordRepository.findAllByDeletedAtIsNull(pageable);
        } else {
            wordPage = wordRepository.searchActiveByKeyword(keyword, pageable);
        }

        Page<WordResponse> responses = wordPage.map(WordResponse::from);
        return new CustomPageResponse<>(responses);
    }


    // === PRIVATE HELPER METHOD ===

    private void validateDuplicateWord(String term) {
        if (wordRepository.existsByTermAndDeletedAtIsNull(term)) {
            throw new IllegalArgumentException("이미 등록된 단어입니다: " + term);
        }
    }

    private List<Kanji> findAndValidateKanjis(List<String> characters) {
        // 1. 한자가 없는 단어(히라가나/가타카나)의 경우, 빈 리스트 반환
        if (characters == null || characters.isEmpty()) {
            return List.of();
        }

        // 2. 한자가 있다면, DB에서 *활성* 한자만 조회
        return characters.stream()
                .map(character -> kanjiRepository.findByCharacterAndDeletedAtIsNull(character)
                        // 3. DB에 없는 한자가 요청되면 즉시 예외 발생
                        .orElseThrow(() -> new IllegalArgumentException(
                                "한자 사전에 등록되지 않은 한자입니다: '" + character + "'. 한자를 먼저 등록해주세요."
                        )))
                .toList();
    }

    // Word와 Kanji 목록을 받아 WordKanji (조인 테이블) 레코드를 생성
    private void linkKanjisToWord(Word word, List<Kanji> kanjis) {
        for (Kanji kanji : kanjis) {
            WordKanji wordKanji = new WordKanji(word, kanji);
            wordKanjiRepository.save(wordKanji);
            word.getWordKanjis().add(wordKanji); // (양방향 연관관계)
        }
    }

    // 단어 수정 시, 기존의 WordKanji 연결을 모두 삭제
    private void clearKanjiLinks(Word word) {
        wordKanjiRepository.deleteAllByWord(word); // DB에서 삭제
        word.getWordKanjis().clear(); // 엔티티(1차 캐시)에서도 삭제
    }
}