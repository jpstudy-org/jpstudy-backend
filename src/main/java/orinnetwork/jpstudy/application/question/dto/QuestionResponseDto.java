package orinnetwork.jpstudy.application.question.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Question;

public record QuestionResponseDto(
        Long questionId,
        String questionText,
        String passage,
        String audioUrl,
        List<ChoiceDto> choices
) {
    public record ChoiceDto(
            Long choiceId,
            String choiceText
    ) {}

    public static QuestionResponseDto fromEntity(Question question) {
        List<ChoiceDto> choiceDtos = question.getChoices().stream()
                .map(choice -> new ChoiceDto(choice.getId(), choice.getChoiceText()))
                .toList();

        return new QuestionResponseDto(
                question.getId(),
                question.getQuestionText(),
                question.getPassage(),
                question.getAudioUrl(),
                choiceDtos
        );
    }
}
