package orinnetwork.jpstudy.presentation.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.exam.ExamService;
import orinnetwork.jpstudy.application.exam.dto.ExamResponse;
import orinnetwork.jpstudy.application.admin.question.dto.CreateExamRequest;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ExamTakingResponse> createRandomExam(@RequestBody CreateExamRequest request) {
        ExamTakingResponse examResponse = examService.createExamFromBlueprint(request);
        return ResponseEntity.ok(examResponse);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamTakingResponse> getExam(@PathVariable Long examId) {
        ExamTakingResponse examResponse = examService.getExamDetails(examId);

        return ResponseEntity.ok(examResponse);
    }
}