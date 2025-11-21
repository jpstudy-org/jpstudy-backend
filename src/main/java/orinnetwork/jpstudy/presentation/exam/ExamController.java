package orinnetwork.jpstudy.presentation.exam;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.exam.ExamBlueprintService;
import orinnetwork.jpstudy.application.admin.exam.dto.BlueprintResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.exam.ExamService;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamBlueprintService blueprintService;

    @GetMapping("/ongoing")
    public ResponseEntity<StartTestResponse> getOngoingExam(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StartTestResponse response = examService.getOngoingExam(userDetails.getMemberId());

        if (response == null) {
            return ResponseEntity.noContent().build(); // 204: 없음
        }

        return ResponseEntity.ok(response); // 200: 있음 (데이터 포함)
    }

    @GetMapping("/blueprints")
    public ResponseEntity<CustomPageResponse<BlueprintResponse>> getAvailableBlueprints(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(blueprintService.getAllBlueprints(pageable));
    }

    @PostMapping("/start/{blueprintId}")
    public ResponseEntity<StartTestResponse> startExam(
            @PathVariable Long blueprintId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String examTitle = "모의고사 (" + LocalDate.now() + ")";

        StartTestResponse response = examService.startExamByBlueprint(
                blueprintId,
                userDetails.getMemberId(),
                examTitle
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamTakingResponse> getExamDetails(@PathVariable Long examId) {
        // ExamService에 만들어둔 getExamDetails 메서드 호출
        ExamTakingResponse response = examService.getExamDetails(examId);
        return ResponseEntity.ok(response);
    }
}