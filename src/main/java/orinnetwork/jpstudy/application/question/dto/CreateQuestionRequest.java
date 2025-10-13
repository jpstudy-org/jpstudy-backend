package orinnetwork.jpstudy.application.question.dto;

import java.util.List;

public record CreateQuestionRequest(
        Long levelId,
        Long categoryId,
        String questionText,
        String explanation,
        List<ChoiceDto> choices
) {}
