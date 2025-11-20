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
import orinnetwork.jpstudy.application.progress.kanji.KanjiProgressService;
import orinnetwork.jpstudy.application.progress.kanji.dto.ReviewRequest;
import orinnetwork.jpstudy.application.progress.kanji.dto.StudySessionResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api/progress/kanji")
@RequiredArgsConstructor
public class KanjiProgressController {

    private final KanjiProgressService kanjiProgressService;

    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = kanjiProgressService.getStudySession(memberId);

        return ResponseEntity.ok(session);
    }

    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long memberId = userDetails.getMemberId();
        kanjiProgressService.updateProgress(
                memberId,
                request.getKanjiId(),
                request.getDifficulty()
        );
        return ResponseEntity.ok().build();
    }
}
