package orinnetwork.jpstudy.presentation.progress;

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

@RestController
@RequestMapping("/api/progress/word")
@RequiredArgsConstructor
public class WordProgressController {

    private final WordProgressService wordProgressService;

    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = wordProgressService.getStudySession(memberId);

        return ResponseEntity.ok(session);
    }

    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(
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
