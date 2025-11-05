package orinnetwork.jpstudy.presentation.admin.dictionary.word;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.dictionary.word.AdminWordService;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordRequest;
import orinnetwork.jpstudy.application.admin.dictionary.word.dto.WordResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;

@RestController
@RequestMapping("/api/admin/word")
@RequiredArgsConstructor
public class AdminWordController {

    private final AdminWordService adminWordService;

    /**
     * 단어 생성 (단일)
     * @param wordRequest 단어 정보
     */
    @PostMapping
    public ResponseEntity<WordResponse> createWord(@RequestBody WordRequest wordRequest) {
        WordResponse response = adminWordService.createWord(wordRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * 단어 생성 (다중)
     * @param wordRequests 단어 정보 리스트
     */
    @PostMapping("/mult")
    public ResponseEntity<List<WordResponse>> createWords(@RequestBody List<WordRequest> wordRequests) {
        List<WordResponse> responses = adminWordService.createOrUpdateWords(wordRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 단어 조회 (페이징)
     * @param keyword 검색어
     * @param pageable 페이징 정보
     */
    @GetMapping
    public ResponseEntity<CustomPageResponse<WordResponse>> getWords(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        CustomPageResponse<WordResponse> wordPage = adminWordService.getWords(keyword, pageable);
        return ResponseEntity.ok(wordPage);
    }

    /**
     * 단어 수정
     * @param wordRequest 수정 정보
     */
    @PutMapping
    public ResponseEntity<WordResponse> updateWord(@RequestBody WordRequest wordRequest) {
        WordResponse response = adminWordService.updateWord(wordRequest);
        return ResponseEntity.ok(response);
    }


    /**
     * 단어 삭제
     * @param wordRequest 삭제 할 Word 정보
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteWord(@RequestBody WordRequest wordRequest) {
        adminWordService.deleteWord(wordRequest);
        return ResponseEntity.noContent().build();
    }
}