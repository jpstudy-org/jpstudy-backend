package orinnetwork.jpstudy.application.exam;

import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.dto.ExamResponseDto;
import orinnetwork.jpstudy.application.question.dto.CreateExamRequest;
import orinnetwork.jpstudy.application.question.dto.QuestionResponseDto;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final LevelRepository levelRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional
    public ExamResponseDto createRandomExam(CreateExamRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨입니다."));

        Exam exam = Exam.builder()
                .level(level)
                .title(request.title())
                .totalTimeMinutes(120)
                .build();

        examRepository.save(exam);

        // 2. 모든 문제를 추출하여 하나의 리스트에 바로 담기
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(questionRepository.findRandomQuestionsByLevelAndCategory(level.getId(), "독해-단문", 5));
        allQuestions.addAll(questionRepository.findRandomQuestionsByLevelAndCategory(level.getId(), "문법", 10));

        // 3. 추출된 문제들을 Exam과 연결
        int questionNumber = 1;
        for (Question q : allQuestions) { // 합쳐진 리스트를 사용해 한 번만 반복
            ExamQuestion examQuestion = new ExamQuestion(exam, q, questionNumber++);
            examQuestionRepository.save(examQuestion);
        }

        // 4. Question 엔티티 리스트를 QuestionResponseDto 리스트로 변환
        List<QuestionResponseDto> questionDtos = allQuestions.stream()
                .map(QuestionResponseDto::fromEntity)
                .toList();

        return ExamResponseDto.of(exam, questionDtos);
    }

    /**
     * 특정 시험지의 상세 정보(문제 목록 포함)를 조회합니다.
     * @param examId 조회할 시험지의 ID
     * @return 시험지 정보와 문제 DTO 목록이 포함된 ExamResponseDto
     */
    public ExamResponseDto getExamDetails(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("시험지를 찾을 수 없습니다."));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionNumberAsc(examId);

        List<QuestionResponseDto> questionDtos = examQuestions.stream()
                .map(examQuestion -> QuestionResponseDto.fromEntity(examQuestion.getQuestion()))
                .toList();

        return ExamResponseDto.of(exam, questionDtos);
    }
}
