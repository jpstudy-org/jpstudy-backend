package orinnetwork.jpstudy.application.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.component.TestAnswerManager;
import orinnetwork.jpstudy.application.exam.component.TestGrader;
import orinnetwork.jpstudy.application.exam.dto.GradeResult;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.application.exam.dto.SubmitTestRequest;
import orinnetwork.jpstudy.application.exam.dto.TestResultResponse;
import orinnetwork.jpstudy.domain.exam.AttemptStatus;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.exam.MemberAnswerRepository;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.exam.TestAttemptRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class TestAttemptService {

    private final MemberRepository memberRepository;
    private final ExamRepository examRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final MemberAnswerRepository memberAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final TestAnswerManager testAnswerManager;
    private final TestGrader testGrader;

    @Transactional
    public void saveAnswer(Long attemptId, Long memberId, Long questionId, Long choiceId) {
        TestAttempt attempt = getAttemptWithOwnership(attemptId, memberId);
        testAnswerManager.upsertAnswer(attempt, questionId, choiceId);
    }

    public StartTestResponse startTest(Long examId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        TestAttempt attempt = testAttemptRepository.save(
                TestAttempt.start(member, exam)
        );

        return StartTestResponse.of(attempt,
                examQuestionRepository.findByExamIdOrderByQuestionNumberAsc(examId),
                java.util.List.of());
    }

    public TestResultResponse submitTest(Long attemptId, Long memberId, SubmitTestRequest request) {
        TestAttempt attempt = getAttemptWithOwnership(attemptId, memberId);
        validateNotSubmitted(attempt);

        GradeResult gradeResult = testGrader.grade(attempt, request.answers());

        memberAnswerRepository.saveAll(gradeResult.answers());
        attempt.complete(gradeResult.score());

        return createResultResponse(attempt);
    }

    private TestAttempt getAttemptWithOwnership(Long attemptId, Long memberId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEST_ATTEMPT_NOT_FOUND));

        if (!attempt.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.EXAM_NOT_OWNER);
        }
        return attempt;
    }

    private void validateNotSubmitted(TestAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.COMPLETED) {
            throw new CustomException(ErrorCode.TEST_ALREADY_SUBMITTED);
        }
    }

    private TestResultResponse createResultResponse(TestAttempt attempt) {
        int totalQuestions = examQuestionRepository.countByExamId(attempt.getExam().getId());
        return new TestResultResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                attempt.getScore(),
                totalQuestions,
                attempt.getStartTime(),
                attempt.getEndTime(),
                java.util.List.of()
        );
    }
}