package orinnetwork.jpstudy.presentation.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.exam.TestAttemptService;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.application.exam.dto.SubmitTestRequest;
import orinnetwork.jpstudy.application.exam.dto.TestResultResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    @PostMapping("/start/{examId}")
    public ResponseEntity<StartTestResponse> startTest(
            @PathVariable Long examId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        Long memberId = userDetails.getMemberId();
        StartTestResponse response = testAttemptService.startTest(examId, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit/{attemptId}")
    public ResponseEntity<TestResultResponse> submitTest(
            @PathVariable Long attemptId,
            @RequestBody SubmitTestRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        Long memberId = userDetails.getMemberId();
        TestResultResponse result = testAttemptService.submitTest(attemptId, memberId, request);
        return ResponseEntity.ok(result);
    }
}
