package orinnetwork.jpstudy.presentation.exam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.exam.TestAttemptService;
import orinnetwork.jpstudy.application.exam.dto.SaveAnswerRequest;
import orinnetwork.jpstudy.application.exam.dto.SubmitTestRequest;
import orinnetwork.jpstudy.application.exam.dto.TestResultResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Test Attempt API", description = "모의고사 응시 과정 관리 (답변 저장 및 제출)")
@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    @Operation(summary = "답변 저장", description = "진행 중인 시험에 대한 특정 문항의 답안을 임시로 저장합니다.")
    @PostMapping("/{attemptId}/answer")
    public ResponseEntity<Void> saveAnswer(
            @Parameter(description = "응시 ID")
            @PathVariable Long attemptId,

            @RequestBody SaveAnswerRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        testAttemptService.saveAnswer(attemptId, userDetails.getMemberId(), request.questionId(), request.choiceId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시험 최종 제출", description = "진행 중인 시험을 최종적으로 제출하고, 시험 결과를 반환합니다.")
    @PostMapping("/submit/{attemptId}")
    public ResponseEntity<TestResultResponse> submitTest(
            @Parameter(description = "응시 ID")
            @PathVariable Long attemptId,

            @RequestBody SubmitTestRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long memberId = userDetails.getMemberId();
        TestResultResponse result = testAttemptService.submitTest(attemptId, memberId, request);
        return ResponseEntity.ok(result);
    }
}
