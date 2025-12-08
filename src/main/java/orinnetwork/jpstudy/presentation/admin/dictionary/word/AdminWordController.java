package orinnetwork.jpstudy.presentation.admin.dictionary.word;

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
import orinnetwork.jpstudy.application.admin.dictionary.word.WordService;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordRequest;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;

@Tag(name = "Admin - Dictionary (Word)", description = "관리자: 단어 사전 생성, 조회 및 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/word")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminWordController {

    private final WordService wordService;

    @Operation(summary = "단어 생성 (단일)", description = "새로운 단어 정보를 단일로 생성합니다.")
    @PostMapping
    public ResponseEntity<WordResponse> createWord(@RequestBody WordRequest wordRequest) {
        WordResponse response = wordService.createWord(wordRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "단어 생성 및 수정 (다중)", description = "단어 정보 리스트를 받아 일괄적으로 생성하거나 기존 단어를 수정합니다.")
    @PostMapping("/mult")
    public ResponseEntity<List<WordResponse>> createWords(@RequestBody List<WordRequest> wordRequests) {
        List<WordResponse> responses = wordService.createOrUpdateWords(wordRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "단어 목록 검색 및 조회", description = "키워드를 통해 단어를 검색하고 결과를 페이지네이션하여 반환합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<WordResponse>> getWords(
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,

            @ParameterObject
            @PageableDefault(size = 15) Pageable pageable) {

        CustomPageResponse<WordResponse> wordPage = wordService.getWords(keyword, pageable);
        return ResponseEntity.ok(wordPage);
    }

    @Operation(summary = "단어 수정", description = "단어 ID를 포함하여 기존 단어 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<WordResponse> updateWord(@RequestBody WordRequest wordRequest) {
        WordResponse response = wordService.updateWord(wordRequest);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "단어 삭제", description = "단어 ID를 포함한 요청 정보를 받아 해당 단어를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteWord(@RequestBody WordRequest wordRequest) {
        wordService.deleteWord(wordRequest);
        return ResponseEntity.noContent().build();
    }
}