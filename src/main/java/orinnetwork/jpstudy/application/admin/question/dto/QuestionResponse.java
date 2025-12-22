package orinnetwork.jpstudy.application.admin.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Question;

@Schema(description = "관리자용 문제 상세 정보 응답 DTO (정답 포함)")
public record QuestionResponse(
        @Schema(description = "문제의 고유 ID")
        Long questionId,

        @Schema(description = "문제의 난이도 레벨 ID")
        Long levelId,

        @Schema(description = "문제의 카테고리 ID")
        Long categoryId,

        @Schema(description = "문제 본문 내용")
        String questionText,

        @Schema(description = "지문 내용 (독해 지문 등, 선택 사항)", nullable = true)
        String passage,

        @Schema(description = "오디오 파일 URL (청해 문제용, 선택 사항)", nullable = true)
        String audioUrl,

        @Schema(description = "선택지 목록 (정답 여부 포함)")
        List<ChoiceDto> choices
) {
    @Schema(description = "문제 선택지 상세 정보 DTO (정답 여부 포함)")
    public record ChoiceDto(
            @Schema(description = "선택지의 고유 ID")
            Long choiceId,

            @Schema(description = "선택지 내용 텍스트")
            String choiceText,

            @Schema(description = "이 선택지가 정답인지 여부 (true/false)")
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
