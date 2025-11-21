package orinnetwork.jpstudy.application.exam.dto;

import jakarta.validation.constraints.NotNull;

public record SaveAnswerRequest(
        @NotNull Long questionId,
        @NotNull Long choiceId
) {
}
