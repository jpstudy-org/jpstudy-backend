package orinnetwork.jpstudy.presentation.admin.dictionary.kanji;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.AdminKanjiService;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiRequest;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiResponse;

@RestController
@RequestMapping("/api/admin/kanji")
@RequiredArgsConstructor
public class AdminKanjiController {

    private final AdminKanjiService adminKanjiService;

    /**
     * 한자 생성 (단일)
     * @param kanjiRequest 한자 정보
     */
    @PostMapping
    public ResponseEntity<KanjiResponse> createKanji(@RequestBody KanjiRequest kanjiRequest) {
        KanjiResponse response = adminKanjiService.createKanji(kanjiRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 한자 생성 (다중)
     * @param kanjiRequests 한자 정보 리스트
     */
    @PostMapping("/mult")
    public ResponseEntity<List<KanjiResponse>> createKanjis(@RequestBody List<KanjiRequest> kanjiRequests) {
        List<KanjiResponse> responses = adminKanjiService.createOrUpdateKanjisFromCSV(kanjiRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }


    /**
     * 한자 조회 (페이징)
     * @param keyword 검색어 (한자, 뜻, 음독, 훈독)
     * @param pageable 페이징 정보 (page, size, sort)
     */
    @GetMapping
    public ResponseEntity<Page<KanjiResponse>> getKanjis(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<KanjiResponse> kanjiPage = adminKanjiService.getKanjis(keyword, pageable);
        return ResponseEntity.ok(kanjiPage);
    }

    /**
     * 한자 수정
     * @param kanjiRequest 수정 정보
     */
    @PutMapping
    public ResponseEntity<KanjiResponse> updateKanji(@RequestBody KanjiRequest kanjiRequest) {
        KanjiResponse response = adminKanjiService.updateKanji(kanjiRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 한자 삭제
     * @param id 삭제 할 ID
     */
    public ResponseEntity<Void> deleteKanji(@PathVariable Long id) {
        adminKanjiService.deleteKanji(id);
        return ResponseEntity.noContent().build();
    }
}