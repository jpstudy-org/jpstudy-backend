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
import orinnetwork.jpstudy.application.progress.kanji.KanjiProgressService;
import orinnetwork.jpstudy.application.progress.kanji.dto.ReviewRequest;
import orinnetwork.jpstudy.application.progress.kanji.dto.StudySessionResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Tag(name = "Kanji Progress API", description = "한자 학습 진도 및 복습 세션 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/progress/kanji")
@RequiredArgsConstructor
public class KanjiProgressController {

    private final KanjiProgressService kanjiProgressService;

    @Operation(summary = "한자 학습 세션 가져오기", description = "사용자에게 할당된 새로운 학습 또는 복습 한자 세션을 조회합니다.")
    @GetMapping("/session")
    public ResponseEntity<StudySessionResponse> getStudySession(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        StudySessionResponse session = kanjiProgressService.getStudySession(memberId);

        return ResponseEntity.ok(session);
    }

    @Operation(summary = "한자 복습 결과 제출", description = "사용자가 복습한 한자의 난이도 평가 결과를 저장하고 진도를 업데이트합니다.")
    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(
            @Parameter(hidden = true)
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
