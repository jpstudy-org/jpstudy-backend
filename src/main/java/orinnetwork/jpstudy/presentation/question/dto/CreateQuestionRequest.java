package orinnetwork.jpstudy.presentation.question.dto;

import java.util.List;

public record CreateQuestionRequest(
        Long levelId,
        Long categoryId,
        String questionText,
        String passage,
        String audioUrl,
        String explanation,
        List<ChoiceDto> choices
) {
    public record ChoiceDto(
            String choiceText,
            boolean isCorrect
    ) {}
}
