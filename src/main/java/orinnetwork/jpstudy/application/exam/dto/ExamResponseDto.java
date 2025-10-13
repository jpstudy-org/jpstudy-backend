package orinnetwork.jpstudy.application.exam.dto;

import java.util.List;
import orinnetwork.jpstudy.application.question.dto.QuestionResponseDto;
import orinnetwork.jpstudy.domain.exam.Exam;

public record ExamResponseDto(
        Long examId,
        String title,
        String levelName,
        int totalTimeMinutes,
        List<QuestionResponseDto> questions
) {
    public static ExamResponseDto of(Exam exam, List<QuestionResponseDto> questions) {
        return new ExamResponseDto(
                exam.getId(),
                exam.getTitle(),
                exam.getLevel().getName(),
                exam.getTotalTimeMinutes(),
                questions
        );
    }
}
