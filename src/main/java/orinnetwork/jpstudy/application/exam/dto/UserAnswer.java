package orinnetwork.jpstudy.application.exam.dto;

public record UserAnswer (
    Long questionId,
    Long selectedChoiceId
) {}