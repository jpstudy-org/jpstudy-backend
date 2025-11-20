package orinnetwork.jpstudy.application.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.application.exam.dto.SubmitTestRequest;
import orinnetwork.jpstudy.application.exam.dto.TestResultResponse;
import orinnetwork.jpstudy.application.exam.dto.UserAnswer;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;
import orinnetwork.jpstudy.domain.exam.MemberAnswerRepository;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.exam.TestAttempt.AttemptStatus;
import orinnetwork.jpstudy.domain.exam.TestAttemptRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.ChoiceRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class TestAttemptService {

    private final MemberRepository memberRepository;
    private final ExamRepository examRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final MemberAnswerRepository memberAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public StartTestResponse startTest(Long examId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        TestAttempt attempt = TestAttempt.builder()
                .member(member)
                .exam(exam)
                .build();

        testAttemptRepository.save(attempt);

        return StartTestResponse.of(attempt);
    }

    public TestResultResponse submitTest(Long attemptId, Long memberId, SubmitTestRequest request) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEST_ATTEMPT_NOT_FOUND));

        if (!attempt.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.EXAM_NOT_OWNER);
        }

        if (attempt.getStatus() == AttemptStatus.COMPLETED) {
            throw new CustomException(ErrorCode.TEST_ALREADY_SUBMITTED);
        }

        List<UserAnswer> userAnswers = request.answers();
        List<Long> questionIds = userAnswers.stream().map(UserAnswer::questionId).toList();
        List<Long> choiceIds = userAnswers.stream().map(UserAnswer::selectedChoiceId).toList();

        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        Map<Long, Choice> choiceMap = choiceRepository.findAllById(choiceIds).stream()
                .collect(Collectors.toMap(Choice::getId, Function.identity()));

        int correctCount = 0;
        List<MemberAnswer> memberAnswers = new ArrayList<>();

        for (UserAnswer userAnswer : userAnswers) {
            Question question = questionMap.get(userAnswer.questionId());
            Choice selectedChoice = choiceMap.get(userAnswer.selectedChoiceId());

            boolean isCorrect = false;
            if (question != null && selectedChoice != null) {
                if (selectedChoice.getQuestion().getId().equals(question.getId())) {
                    isCorrect = selectedChoice.isCorrect();
                }
            }

            if (isCorrect) {
                correctCount++;
            }

            MemberAnswer memberAnswer = MemberAnswer.builder()
                    .testAttempt(attempt)
                    .question(question)
                    .selectedChoice(selectedChoice)
                    .isCorrect(isCorrect)
                    .build();

            memberAnswers.add(memberAnswer);
        }

        memberAnswerRepository.saveAll(memberAnswers);

        attempt.complete(correctCount);

        int totalQuestions = examQuestionRepository.countByExamId(attempt.getExam().getId());

        return new TestResultResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                attempt.getScore(),
                totalQuestions,
                attempt.getStartTime(),
                attempt.getEndTime(),
                List.of()
        );
    }
}