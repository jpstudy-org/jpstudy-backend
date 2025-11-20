package orinnetwork.jpstudy.presentation.admin.question;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.question.QuestionService;
import orinnetwork.jpstudy.application.admin.question.dto.QuestionResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.presentation.admin.question.dto.CreateQuestionRequest;
import orinnetwork.jpstudy.presentation.admin.question.dto.UpdateQuestionRequest;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 새로운 문제 생성 (단일)
     *
     * @param request 문제 내용, 선택지, 정답, 레벨, 카테고리 정보
     * @return 생성된 문제의 상세 정보
     */
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody CreateQuestionRequest request) {
        QuestionResponse createdQuestion = questionService.createQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    /**
     * 새로운 문제 생성 (다중)
     */
    @PostMapping("/mult")
    public ResponseEntity<List<QuestionResponse>> createQuestions(
            @RequestBody List<CreateQuestionRequest> questionRequests) {
        List<QuestionResponse> responses = questionService.createOrUpdateQuestionsFromCSV(questionRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 문제 조회 페이징
     *
     * @param pageable 페이징 번호
     * @return Pages
     */
    @GetMapping
    public ResponseEntity<CustomPageResponse<QuestionResponse>> getQuestions(
            @PageableDefault(size = 10) Pageable pageable) {

        CustomPageResponse<QuestionResponse> questionPage = questionService.getQuestions(pageable);
        return ResponseEntity.ok(questionPage);
    }

    /**
     * 특정 문제를 조회하는 API
     *
     * @param questionId 조회할 문제의 ID
     * @return 문제 상세 정보
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long questionId) {
        QuestionResponse question = questionService.getQuestion(questionId);
        return ResponseEntity.ok(question);
    }

    /**
     * 특정 문제를 수정하는 API (관리자용)
     *
     * @param questionId 수정할 문제의 ID
     * @param request    수정할 내용
     * @return 수정된 문제의 상세 정보
     */
    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId,
                                                           @RequestBody UpdateQuestionRequest request) {
        // UpdateQuestionRequest를 별도로 만드는 것이 더 좋지만, 예시에서는 Create DTO를 재사용
        QuestionResponse updatedQuestion = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(updatedQuestion);
    }

    /**
     * 특정 문제를 삭제하는 API (관리자용)
     *
     * @param questionId 삭제할 문제의 ID
     * @return 성공 시 내용 없음(No Content) 응답
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
