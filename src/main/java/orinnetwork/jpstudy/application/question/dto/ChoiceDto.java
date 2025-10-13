package orinnetwork.jpstudy.application.question.dto;

public record ChoiceDto(
        Long choiceId,
        String text,
        boolean isCorrect
) {}