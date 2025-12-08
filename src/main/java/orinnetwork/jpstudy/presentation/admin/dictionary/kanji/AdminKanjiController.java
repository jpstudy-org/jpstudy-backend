package orinnetwork.jpstudy.presentation.admin.dictionary.kanji;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Admin - Dictionary (Kanji)", description = "관리자: 한자 사전 생성, 조회 및 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/kanji")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminKanjiController {

    private final KanjiService kanjiService;

    @Operation(summary = "한자 생성 (단일)", description = "새로운 한자 데이터를 단일로 생성합니다.")
    @PostMapping
    public ResponseEntity<KanjiResponse> createKanji(@RequestBody KanjiRequest kanjiRequest) {

        KanjiResponse response = kanjiService.createKanji(kanjiRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "한자 생성 및 수정 (다중)", description = "한자 데이터 리스트를 받아 일괄적으로 생성하거나 기존 한자를 수정합니다.")
    @PostMapping("/mult")
    public ResponseEntity<List<KanjiResponse>> createKanjis(@RequestBody List<KanjiRequest> kanjiRequests) {

        List<KanjiResponse> responses = kanjiService.createOrUpdateKanjisFromCSV(kanjiRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "한자 목록 검색 및 조회", description = "키워드를 통해 한자를 검색하고 결과를 페이지네이션하여 반환합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<KanjiResponse>> getKanjis(
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,

            @ParameterObject
            @PageableDefault(size = 10) Pageable pageable) {

        CustomPageResponse<KanjiResponse> kanjiPage = kanjiService.getKanjis(keyword, pageable);
        return ResponseEntity.ok(kanjiPage);
    }

    @Operation(summary = "한자 수정", description = "한자 ID를 포함하여 기존 한자 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<KanjiResponse> updateKanji(@RequestBody KanjiRequest kanjiRequest) {

        KanjiResponse response = kanjiService.updateKanji(kanjiRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "한자 삭제", description = "한자 ID를 포함한 요청 정보를 받아 해당 한자를 삭제(비활성화) 처리합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteKanji(@RequestBody KanjiRequest kanjiRequest) {
        kanjiService.deleteKanji(kanjiRequest);
        return ResponseEntity.noContent().build();
    }
}