package orinnetwork.jpstudy.application.admin.dictionary.kanji;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiRequest;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminKanjiService {

    private final KanjiRepository kanjiRepository;

    // 단어 추가
    @Transactional
    public KanjiResponse createKanji(KanjiRequest kanjiRequest) {

        validateDuplicateKanji(kanjiRequest.getCharacter());

        Kanji createkanji = Kanji.builder()
                .character(kanjiRequest.getCharacter())
                .meaning(kanjiRequest.getMeaning())
                .meaningEn(kanjiRequest.getMeaningEn())
                .onyomi(kanjiRequest.getOnyomi())
                .kunyomi(kanjiRequest.getKunyomi())
                .strokeCount(kanjiRequest.getStrokeCount())
                .radical(kanjiRequest.getRadical())
                .level(kanjiRequest.getLevel())
                .build();

        Kanji savedKanji = kanjiRepository.save(createkanji);

        return KanjiResponse.from(savedKanji);
    }

    // 단어 추가 (다중)
    @Transactional
    public List<KanjiResponse> createOrUpdateKanjisFromCSV(List<KanjiRequest> requests) {

        List<KanjiResponse> responses = new ArrayList<>();

        for (KanjiRequest req : requests) {
            String character = req.getCharacter();

            Kanji kanji = kanjiRepository.findByCharacter(character)
                    .orElse(null);

            Kanji savedKanji;

            if (kanji != null) {
                kanji.updateDetails(
                        req.getMeaning(),
                        req.getMeaningEn(),
                        req.getOnyomi(),
                        req.getKunyomi(),
                        req.getStrokeCount(),
                        req.getRadical(),
                        req.getLevel()
                );
                savedKanji = kanji;
            }
            else {
                Kanji newKanji = req.toEntity();
                savedKanji = kanjiRepository.save(newKanji);
            }

            responses.add(KanjiResponse.from(savedKanji));
        }
        return responses;
    }

    // 단어 삭제
    @Transactional
    public void deleteKanji(Long id) {
        Kanji kanji = kanjiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 한자는 존재하지 않습니다."));

        kanji.softDelete();
    }

    // 단어 수정
    @Transactional
    public KanjiResponse updateKanji(KanjiRequest kanjiRequest) {
        Kanji kanji = kanjiRepository.findByCharacter(kanjiRequest.getCharacter())
                .orElseThrow(() -> new IllegalArgumentException("해당 한자는 존재하지 않습니다."));

        kanji.updateDetails(
                kanjiRequest.getMeaning(),
                kanjiRequest.getMeaningEn(),
                kanjiRequest.getOnyomi(),
                kanjiRequest.getKunyomi(),
                kanjiRequest.getStrokeCount(),
                kanjiRequest.getRadical(),
                kanjiRequest.getLevel()
        );

        return KanjiResponse.from(kanji);
    }

    // 단어 조회 (페이징/검색)
    @Transactional(readOnly = true)
    public Page<KanjiResponse> getKanjis(String keyword, Pageable pageable) {
        Page<Kanji> kanjiPage;

        if (keyword == null || keyword.isBlank()) {
            kanjiPage = kanjiRepository.findAll(pageable);
        } else {
            kanjiPage = kanjiRepository.searchActiveByKeyword(keyword, pageable);
        }

        return kanjiPage.map(KanjiResponse::from);
    }

    // 중복 검사
    private void validateDuplicateKanji(String character) {
        if (kanjiRepository.existsByCharacterAndDeletedAtIsNull(character)) {
            throw new IllegalArgumentException("이미 등록된 한자입니다: " + character);
        }
    }
}