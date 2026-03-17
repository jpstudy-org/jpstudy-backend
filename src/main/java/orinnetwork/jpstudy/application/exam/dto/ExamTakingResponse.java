package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.questionbank.Question;

@Schema(description = "시험 응시를 위해 클라이언트에게 제공되는 문제 목록 및 메타 정보 응답 DTO")
public record ExamTakingResponse(
        @Schema(description = "응시할 시험의 Blueprint ID")
        Long examId,

        @Schema(description = "응시할 시험의 제목")
        String title,

        @Schema(description = "시험의 난이도 레벨 이름")
        String levelName,

        @Schema(description = "총 응시 가능 시간 (분 단위)")
        int totalTimeMinutes,

        @Schema(description = "시험에 포함된 문제 목록 (순서 보장)")
        List<QuestionDto> questions
) {
    @Schema(description = "개별 시험 문제 상세 정보 DTO")
    public record QuestionDto(
            @Schema(description = "문제의 고유 ID")
            Long questionId,

            @Schema(description = "시험 내 문제 번호 (순서)")
            int number,

            @Schema(description = "문제 본문 내용")
            String content,

            @Schema(description = "독해 지문 (선택 사항)", nullable = true)
            String passage,

            @Schema(description = "청해 문제용 오디오 파일 URL (선택 사항)", nullable = true)
            String audioUrl,

            @Schema(description = "문제 선택지 목록")
            List<ChoiceDto> choices
    ) {
    }

    @Schema(description = "문제 선택지 정보 DTO")
    public record ChoiceDto(
            @Schema(description = "선택지의 고유 ID")
            Long choiceId,

            @Schema(description = "선택지 내용 텍스트")
            String text
    ) {
    }

    public static ExamTakingResponse of(Exam exam, List<ExamQuestion> examQuestions) {
        List<QuestionDto> questionDtos = examQuestions.stream()
                .map(examQuestion -> {
                    Question q = examQuestion.getQuestion();

                    List<ChoiceDto> choiceDtos = q.getChoices().stream()
                            .map(choice -> new ChoiceDto(choice.getId(), choice.getChoiceText()))
                            .toList();

                    return new QuestionDto(
                            q.getId(),
                            examQuestion.getQuestionNumber(),
                            q.getQuestionText(),
                            q.getPassage(),
                            q.getAudioUrl(),
                            choiceDtos
                    );
                })
                .toList();

        return new ExamTakingResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getLevel().getName(),
                exam.getTotalTimeMinutes(),
                questionDtos
        );
    }
}
