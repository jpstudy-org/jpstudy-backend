package orinnetwork.jpstudy.presentation.admin.question.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Choice;

public record UpdateQuestionRequest(
        Long levelId,
        Long categoryId,
        String questionText,
        String passage,
        String audioUrl,
        String explanation,
        List<Choice> choices
) {
    public record ChoiceDto(
            String choiceText,
            boolean isCorrect
    ) {
    }
}
