package orinnetwork.jpstudy.application.exam;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.dto.ExamResponse;
import orinnetwork.jpstudy.application.admin.question.dto.CreateExamRequest;
import orinnetwork.jpstudy.application.admin.question.dto.QuestionResponse;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final LevelRepository levelRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional
    public ExamTakingResponse createRandomExam(CreateExamRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.LEVEL_NOT_FOUND));

        Exam exam = Exam.builder()
                .level(level)
                .title(request.title())
                .totalTimeMinutes(120)
                .build();

        examRepository.save(exam);

        // 2. 모든 문제를 추출하여 하나의 리스트에 바로 담기
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(questionRepository.findRandomQuestionsByLevelAndCategory(
                level.getId(),
                "한자 읽기",
                PageRequest.of(0, 5)
        ));
        allQuestions.addAll(questionRepository.findRandomQuestionsByLevelAndCategory(
                level.getId(),
                "문법 (괄호)",
                PageRequest.of(0, 10)
        ));

        // 3. 추출된 문제들을 Exam과 연결
        List<ExamQuestion> examQuestions = new ArrayList<>();
        int questionNumber = 1;
        for (Question q : allQuestions) {
            ExamQuestion examQuestion = new ExamQuestion(exam, q, questionNumber++);
            examQuestions.add(examQuestion);
        }

        examQuestionRepository.saveAll(examQuestions);

        return ExamTakingResponse.of(exam, examQuestions);
    }

    /**
     * 특정 시험지의 상세 정보(문제 목록 포함)를 조회합니다.
     * @param examId 조회할 시험지의 ID
     * @return 시험지 정보와 문제 DTO 목록이 포함된 ExamResponseDto
     */
    public ExamTakingResponse getExamDetails(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionNumberAsc(examId);

        return ExamTakingResponse.of(exam, examQuestions);
    }
}
