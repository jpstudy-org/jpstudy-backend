package orinnetwork.jpstudy.application.exam.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.questionbank.Question;

public record ExamTakingResponse(
        Long examId,
        String title,
        String levelName,
        int totalTimeMinutes,
        List<QuestionDto> questions
) {
    public record QuestionDto(
            Long questionId,
            int questionNumber,
            String questionText,
            String passage,
            String audioUrl,
            List<ChoiceDto> choices
    ) {}

    public record ChoiceDto(
            Long choiceId,
            String choiceText
    ) {}

    public static ExamTakingResponse of(Exam exam, List<ExamQuestion> examQuestions) {
        List<QuestionDto> questionDtos = examQuestions.stream()
                .map(examQuestion -> {
                    Question q = examQuestion.getQuestion();

                    List<ChoiceDto> choiceDtos = q.getChoices().stream()
                            .map(choice -> new ChoiceDto(choice.getId(), choice.getChoiceText()))
                            .toList();

                    return new QuestionDto(
                            q.getId(),
                            examQuestion.getQuestionNumber(),
                            q.getQuestionText(),
                            q.getPassage(),
                            q.getAudioUrl(),
                            choiceDtos
                    );
                })
                .toList();

        return new ExamTakingResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getLevel().getName(),
                exam.getTotalTimeMinutes(),
                questionDtos
        );
    }
}
