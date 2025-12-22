package orinnetwork.jpstudy.presentation.admin.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.questionbank.Choice;

@Schema(description = "관리자용 문제 수정 요청 DTO")
public record UpdateQuestionRequest(
        @Schema(description = "수정할 난이도 레벨 ID")
        Long levelId,

        @Schema(description = "수정할 카테고리 ID")
        Long categoryId,

        @Schema(description = "수정할 문제 본문 내용")
        String questionText,

        @Schema(description = "수정할 지문 내용 (선택 사항)", nullable = true)
        String passage,

        @Schema(description = "수정할 오디오 파일 URL (선택 사항)", nullable = true)
        String audioUrl,

        @Schema(description = "수정할 정답 해설")
        String explanation,

        @Schema(description = "수정할 선택지 목록")
        List<Choice> choices
) {
    @Schema(description = "문제 선택지 DTO")
    public record ChoiceDto(
            @Schema(description = "선택지 내용")
            String choiceText,

            @Schema(description = "이 선택지가 정답인지 여부")
            boolean isCorrect
    ) {
    }
}
