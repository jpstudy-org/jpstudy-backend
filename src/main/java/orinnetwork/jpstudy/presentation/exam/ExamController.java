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
import orinnetwork.jpstudy.application.exam.dto.ExamResponseDto;
import orinnetwork.jpstudy.application.question.dto.CreateExamRequest;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ExamResponseDto> createRandomExam(@RequestBody CreateExamRequest request) {
        ExamResponseDto examResponse = examService.createRandomExam(request);
        return ResponseEntity.ok(examResponse);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponseDto> getExam(@PathVariable Long examId) {
        ExamResponseDto examResponse = examService.getExamDetails(examId);

        return ResponseEntity.ok(examResponse);
    }
}
