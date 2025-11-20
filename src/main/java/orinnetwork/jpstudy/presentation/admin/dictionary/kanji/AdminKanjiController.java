package orinnetwork.jpstudy.presentation.admin.dictionary.kanji;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.KanjiService;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiRequest;
import orinnetwork.jpstudy.application.admin.dictionary.kanji.dto.KanjiResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;

@RestController
@RequestMapping("/api/admin/kanji")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminKanjiController {

    private final KanjiService kanjiService;

    @PostMapping
    @Operation(summary = "한자 생성 (단일)", description = "한자 데이터를 생성합니다")
    public ResponseEntity<KanjiResponse> createKanji(@RequestBody KanjiRequest kanjiRequest) {

        KanjiResponse response = kanjiService.createKanji(kanjiRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/mult")
    @Operation(summary = "한자 생성 (다중)", description = "한자 데이터를 다중으로 업로드합니다")
    public ResponseEntity<List<KanjiResponse>> createKanjis(@RequestBody List<KanjiRequest> kanjiRequests) {

        List<KanjiResponse> responses = kanjiService.createOrUpdateKanjisFromCSV(kanjiRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }


    @GetMapping
    @Operation(summary = "한자 조회 (페이징)", description = "페이지 형태로 한자 목록을 조회합니다")
    public ResponseEntity<CustomPageResponse<KanjiResponse>> getKanjis(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        CustomPageResponse<KanjiResponse> kanjiPage = kanjiService.getKanjis(keyword, pageable);
        return ResponseEntity.ok(kanjiPage);
    }

    /**
     * 한자 수정
     *
     * @param kanjiRequest 수정 정보
     */
    @PutMapping
    @Operation(summary = "한자 수정", description = "한자 정보를 수정합니다")
    public ResponseEntity<KanjiResponse> updateKanji(@RequestBody KanjiRequest kanjiRequest) {

        KanjiResponse response = kanjiService.updateKanji(kanjiRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 한자 삭제
     *
     * @param kanjiRequest 삭제 할 한자 정보
     */
    @DeleteMapping
    @Operation(summary = "한자 삭제", description = "한자를 삭제합니다 (비활성화 방식)")
    public ResponseEntity<Void> deleteKanji(@RequestBody KanjiRequest kanjiRequest) {
        kanjiService.deleteKanji(kanjiRequest);
        return ResponseEntity.noContent().build();
    }
}