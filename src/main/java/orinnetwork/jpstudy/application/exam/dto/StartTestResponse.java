package orinnetwork.jpstudy.application.exam.dto;

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

public record StartTestResponse(
        Long attemptId,
        Long examId,
        String examTitle,
        int totalTimeMinutes,
        LocalDateTime startTime,
        List<TestQuestionDto> questions,
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
    public record TestQuestionDto(
            Long questionId,
            int number,
            String content, // 지문
            String passage, // 본문 (독해 등)
            String audioUrl, // 청해용
            List<TestChoiceDto> choices // 보기 목록
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
    public record TestChoiceDto(
            Long choiceId,
            String text
            // ★ 중요: isCorrect(정답 여부)는 시험 중에는 절대 보내면 안 됨!
    ) {
        public static TestChoiceDto from(Choice choice) {
            return new TestChoiceDto(
                    choice.getId(),
                    choice.getChoiceText()
            );
        }
    }
}