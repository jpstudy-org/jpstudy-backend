package orinnetwork.jpstudy.application.admin.question.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Question;

public record QuestionResult(
        Long questionId,
        String questionText,
        String passage,
        String audioUrl,
        List<ChoiceResultDto> choices,
        Long correctChoiceId, // 정답 선택지의 ID
        String explanation
) {
    public record ChoiceResultDto(
            Long choiceId,
            String choiceText,
            boolean isCorrect
    ) {
    }

    public static QuestionResult fromEntity(Question question) {
        List<ChoiceResultDto> choiceResultDtos = question.getChoices().stream()
                .map(choice -> new ChoiceResultDto(choice.getId(), choice.getChoiceText(), choice.isCorrect()))
                .toList();

        Long correctId = question.getChoices().stream()
                .filter(Choice::isCorrect)
                .findFirst()
                .map(Choice::getId)
                .orElse(null);

        return new QuestionResult(
                question.getId(),
                question.getQuestionText(),
                question.getPassage(),
                question.getAudioUrl(),
                choiceResultDtos,
                correctId,
                question.getExplanation()
        );
    }
}
