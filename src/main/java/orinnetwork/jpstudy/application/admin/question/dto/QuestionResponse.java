package orinnetwork.jpstudy.application.admin.question.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Question;

public record QuestionResponse(
        Long questionId,
        Long levelId,
        Long categoryId,
        String questionText,
        String passage,
        String audioUrl,
        List<ChoiceDto> choices
) {
    public record ChoiceDto(
            Long choiceId,
            String choiceText,
            boolean isCorrect
    ) {
    }

    public static QuestionResponse fromEntity(Question question) {
        List<ChoiceDto> choiceDtos = question.getChoices().stream()
                .map(choice -> new ChoiceDto(
                        choice.getId(),
                        choice.getChoiceText(),
                        choice.isCorrect()
                ))
                .toList();

        return new QuestionResponse(
                question.getId(),
                question.getLevel().getId(),
                question.getCategory().getId(),
                question.getQuestionText(),
                question.getPassage(),
                question.getAudioUrl(),
                choiceDtos
        );
    }
}
