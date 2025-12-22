package orinnetwork.jpstudy.application.admin.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Question;

@Schema(description = "문제 상세 조회 및 정답 정보 포함 결과 DTO")
public record QuestionResult(
        @Schema(description = "문제의 고유 ID")
        Long questionId,

        @Schema(description = "문제 본문 내용")
        String questionText,

        @Schema(description = "독해 지문 (선택 사항)", nullable = true)
        String passage,

        @Schema(description = "청해 문제용 오디오 파일 URL (선택 사항)", nullable = true)
        String audioUrl,

        @Schema(description = "선택지 목록 (정답 여부 포함)")
        List<ChoiceResultDto> choices,

        @Schema(description = "정답 선택지의 고유 ID")
        Long correctChoiceId,

        @Schema(description = "문제에 대한 상세 해설")
        String explanation
) {
    @Schema(description = "선택지 결과 DTO (정답 여부 포함)")
    public record ChoiceResultDto(
            @Schema(description = "선택지의 고유 ID")
            Long choiceId,

            @Schema(description = "선택지 내용 텍스트")
            String choiceText,

            @Schema(description = "이 선택지가 정답인지 여부")
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
