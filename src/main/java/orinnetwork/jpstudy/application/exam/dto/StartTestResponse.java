package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Question;

@Schema(description = "사용자가 시험 응시 시작 시 받는 전체 시험 정보 응답 DTO")
public record StartTestResponse(
        @Schema(description = "이번 응시 기록의 고유 ID")
        Long attemptId,

        @Schema(description = "응시하는 시험의 Blueprint ID")
        Long examId,

        @Schema(description = "응시하는 시험의 제목")
        String examTitle,

        @Schema(description = "총 응시 가능 시간 (분 단위)")
        int totalTimeMinutes,

        @Schema(description = "시험 시작 시각 (서버 시간)")
        LocalDateTime startTime,

        @Schema(description = "시험에 포함된 문제 목록 (순서 보장)")
        List<TestQuestionDto> questions,

        @Schema(description = "이전에 저장된 답안 목록 (Map<문제ID, 선택지ID> 형태). 없으면 빈 객체")
        Map<Long, Long> savedAnswers
) {
    public static StartTestResponse of(TestAttempt attempt, List<ExamQuestion> examQuestions, List<MemberAnswer> answers) {
        Exam exam = attempt.getExam();

        List<TestQuestionDto> questionDtos = examQuestions.stream()
                .sorted(Comparator.comparingInt(ExamQuestion::getQuestionNumber))
                .map(eq -> TestQuestionDto.from(eq.getQuestion(), eq.getQuestionNumber()))
                .toList();

        Map<Long, Long> savedAnswerMap = (answers == null || answers.isEmpty())
                ? Map.of()
                :answers.stream()
                .collect(Collectors.toMap(
                        a -> a.getQuestion().getId(),
                        a -> a.getSelectedChoice().getId()
                ));

        return new StartTestResponse(
                attempt.getId(),
                exam.getId(),
                exam.getTitle(),
                exam.getTotalTimeMinutes(),
                attempt.getStartTime(),
                questionDtos,
                savedAnswerMap
        );
    }
    @Schema(description = "시험 문제 상세 정보 DTO")
    public record TestQuestionDto(
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
            List<TestChoiceDto> choices
    ) {
        public static TestQuestionDto from(Question question, int number) {
            List<TestChoiceDto> choiceDtos = question.getChoices().stream()
                    .map(TestChoiceDto::from)
                    .toList();

            return new TestQuestionDto(
                    question.getId(),
                    number,
                    question.getQuestionText(), // or getContent() 필드명 확인 필요
                    question.getPassage(),
                    question.getAudioUrl(),
                    choiceDtos
            );
        }
    }
    @Schema(description = "문제 선택지 정보 DTO")
    public record TestChoiceDto(
            @Schema(description = "선택지의 고유 ID")
            Long choiceId,

            @Schema(description = "선택지 내용 텍스트")
            String text
            // 정답 여부(isCorrect)는 의도적으로 제외되어 클라이언트에게 노출되지 않음
    ) {
        public static TestChoiceDto from(Choice choice) {
            return new TestChoiceDto(
                    choice.getId(),
                    choice.getChoiceText()
            );
        }
    }
}