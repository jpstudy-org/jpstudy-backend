package orinnetwork.jpstudy.presentation.admin.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "관리자용 문제 생성 요청 DTO")
public record CreateQuestionRequest(
        @Schema(description = "문제의 난이도 레벨 ID (필수)")
        Long levelId,

        @Schema(description = "문제의 카테고리 ID (필수)")
        Long categoryId,

        @Schema(description = "문제의 본문 내용 (필수)")
        String questionText,

        @Schema(description = "지문 내용 (선택 사항)", nullable = true)
        String passage,

        @Schema(description = "오디오 파일 URL (선택 사항, 리스닝 문제용)", nullable = true)
        String audioUrl,

        @Schema(description = "정답 해설 (필수)")
        String explanation,

        @Schema(description = "선택지 목록 (최소 2개 이상 필요)")
        List<ChoiceDto> choices
) {
    @Schema(description = "문제 선택지 DTO (선택지 내용과 정답 여부)")
    public record ChoiceDto(
            @Schema(description = "선택지 내용 텍스트")
            String choiceText,

            @Schema(description = "이 선택지가 정답이면 true, 아니면 false")
            boolean isCorrect
    ) {
    }
}
