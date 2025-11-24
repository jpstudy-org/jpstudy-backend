package orinnetwork.jpstudy.application.exam;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.component.ExamGenerator;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamBlueprintRepository;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;
import orinnetwork.jpstudy.domain.exam.MemberAnswerRepository;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.exam.TestAttempt.AttemptStatus;
import orinnetwork.jpstudy.domain.exam.TestAttemptRepository;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamBlueprintRepository examBlueprintRepository;
    private final TestAttemptService testAttemptService;
    private final TestAttemptRepository testAttemptRepository;
    private final MemberAnswerRepository memberAnswerRepository;
    private final ExamGenerator examGenerator;

    /**
     * 시험중인 경우 시험지 가져오기
     * TODO: 시간 상 종료되었는데 가져오는 경우가 있어서 종료는 그냥 끝내버리는 로직으로 수정해야 함
     */
    @Transactional
    public StartTestResponse getOngoingExam(Long memberId) {
        return testAttemptRepository.findFirstByMemberIdAndStatusOrderByStartTimeDesc(
                        memberId, AttemptStatus.IN_PROGRESS)
                .map(this::loadExamContext)
                .orElse(null);
    }

    /**
     * 시험 시작
     * TODO: 여긴 왜 DTO안쓰고 그냥 값들 가져오고 있지?
     */
    @Transactional
    public StartTestResponse startExamByBlueprint(Long blueprintId, Long memberId, String examTitle) {
        // 1. 시험지 생성 (아래 메서드 호출)
        ExamTakingResponse examData = createExamFromBlueprint(blueprintId, examTitle);

        // 2. 응시 기록 생성 (TestAttemptService에게 위임)
        return testAttemptService.startTest(examData.examId(), memberId);
    }

    @Transactional
    public ExamTakingResponse createExamFromBlueprint(Long blueprintId, String title) {
        return examGenerator.generate(blueprintId, title);
    }

    /**
     * 특정 시험지의 상세 정보(문제 목록 포함)를 조회합니다.
     *
     * @param examId 조회할 시험지의 ID
     * @return 시험지 정보와 문제 DTO 목록이 포함된 ExamResponseDto
     */
    public ExamTakingResponse getExamDetails(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionNumberAsc(examId);

        return ExamTakingResponse.of(exam, examQuestions);
    }


    // --- Private ---

    private StartTestResponse loadExamContext(TestAttempt attempt) {
        Long examId = attempt.getExam().getId();

        List<ExamQuestion> questions = examQuestionRepository.findByExamIdOrderByQuestionNumberAsc(examId);
        List<MemberAnswer> savedAnswers = memberAnswerRepository.findByTestAttempt(attempt);

        return StartTestResponse.of(attempt, questions, savedAnswers);
    }
}
