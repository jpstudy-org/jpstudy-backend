package orinnetwork.jpstudy.application.exam.dto;

import java.util.List;
import orinnetwork.jpstudy.application.admin.question.dto.QuestionResponse;
import orinnetwork.jpstudy.domain.exam.Exam;

public record ExamResponse(
        Long examId,
        String title,
        String levelName,
        int totalTimeMinutes,
        List<QuestionResponse> questions
) {
    public static ExamResponse of(Exam exam, List<QuestionResponse> questions) {
        return new ExamResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getLevel().getName(),
                exam.getTotalTimeMinutes(),
                questions
        );
    }
}
