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

    @Operation(summary = "학습 세션 가져오기", description = "사용자에게 할당된 새로운 학습 또는 복습 단어 세션을 조회합니다.")
    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = wordProgressService.getStudySession(memberId);

        return ResponseEntity.ok(session);
    }

    @Operation(summary = "복습 결과 제출", description = "사용자가 복습한 단어의 난이도 평가 결과를 저장하고 진도를 업데이트합니다.")
    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid @RequestBody ReviewRequest request
    ) {
        Long memberId = userDetails.getMemberId();
        wordProgressService.updateProgress(
                memberId,
                request.getWordId(),
                request.getDifficulty()
        );
        return ResponseEntity.ok().build();
    }
}
