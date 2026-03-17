package orinnetwork.jpstudy.presentation.progress;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResponse;
import orinnetwork.jpstudy.application.progress.word.WordProgressService;
import orinnetwork.jpstudy.application.progress.word.dto.ReviewRequest;
import orinnetwork.jpstudy.application.progress.word.dto.StudySessionResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Tag(name = "Word Progress API", description = "단어 학습 진도 및 복습 세션 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/progress/word")
@RequiredArgsConstructor
public class WordProgressController {

    private final WordProgressService wordProgressService;

    @Operation(summary = "단어 학습 세션 가져오기",
            description = "활성 세션이 있으면 이어서, 없으면 새 세션(30개)을 생성하여 반환합니다.")
    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = wordProgressService.getStudySession(memberId);
        return ResponseEntity.ok(session);
    }

    @Operation(summary = "추가 학습 요청",
            description = "현재 세션을 완료하고 새로운 30개 블록의 학습 세션을 생성합니다.")
    @PostMapping("/session/add-more")
    public ResponseEntity<StudySessionResponse> addMoreLearning(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = wordProgressService.addMoreLearning(memberId);
        return ResponseEntity.ok(session);
    }

    @Operation(summary = "단어 복습 결과 제출",
            description = "복습 난이도 평가를 저장하고 다음 복습 일정 및 세션 진행 상태를 반환합니다.")
    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> submitReview(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid @RequestBody ReviewRequest request
    ) {
        Long memberId = userDetails.getMemberId();
        ReviewResponse response = wordProgressService.updateProgress(
                memberId,
                request.getWordId(),
                request.getDifficulty()
        );
        return ResponseEntity.ok(response);
    }
}
