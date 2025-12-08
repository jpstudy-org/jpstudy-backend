package orinnetwork.jpstudy.presentation.admin.question;

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

@Tag(name = "Admin - Question Bank", description = "관리자: 문제(Question) 생성, 조회 및 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "새 문제 생성 (단일)", description = "단일 문제의 내용, 선택지, 정답, 레벨, 카테고리 정보를 입력하여 문제를 생성합니다.")
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody CreateQuestionRequest request) {
        QuestionResponse createdQuestion = questionService.createQuestion(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @Operation(summary = "새 문제 생성 (다중/CSV 업로드)", description = "여러 문제의 정보를 리스트 형태로 받아 일괄적으로 문제를 생성하거나 수정합니다.")
    @PostMapping("/mult")
    public ResponseEntity<List<QuestionResponse>> createQuestions(
            @RequestBody List<CreateQuestionRequest> questionRequests) {
        List<QuestionResponse> responses = questionService.createOrUpdateQuestionsFromCSV(questionRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Operation(summary = "문제 목록 페이징 조회", description = "문제 은행에 등록된 문제 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<QuestionResponse>> getQuestions(
            @ParameterObject
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CustomPageResponse<QuestionResponse> questionPage = questionService.getQuestions(pageable);
        return ResponseEntity.ok(questionPage);
    }

    @Operation(summary = "문제 단건 조회", description = "특정 ID의 문제 상세 정보(내용, 선택지, 정답 등)를 조회합니다.")
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @Parameter(description = "조회할 문제 ID")
            @PathVariable Long questionId
    ) {
        QuestionResponse question = questionService.getQuestion(questionId);
        return ResponseEntity.ok(question);
    }

    @Operation(summary = "문제 수정", description = "특정 문제의 내용, 선택지, 정답 정보를 수정합니다.")
    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @Parameter(description = "수정할 문제 ID")
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request
    ) {
        QuestionResponse updatedQuestion = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(updatedQuestion);
    }

    @Operation(summary = "문제 삭제", description = "특정 ID의 문제를 삭제합니다.")
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "삭제할 문제 ID")
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
