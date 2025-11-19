package orinnetwork.jpstudy.application.exam;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.question.dto.CreateExamRequest;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.domain.exam.BlueprintDetail;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamBlueprint;
import orinnetwork.jpstudy.domain.exam.ExamBlueprintRepository;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
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
    private final ExamBlueprintRepository examBlueprintRepository;


    @Transactional
    public ExamTakingResponse createExamFromBlueprint(CreateExamRequest request) {
        ExamBlueprint blueprint = examBlueprintRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        Exam exam = Exam.builder()
                .level(blueprint.getLevel())
                .title(request.title())
                .totalTimeMinutes(blueprint.getTotalTimeMinutes())
                .build();

        examRepository.save(exam);

        List<ExamQuestion> examQuestions = new ArrayList<>();
        int currentQuestionNumber = 1;

        for (BlueprintDetail detail : blueprint.getDetails()) {

            List<Question> questions = questionRepository.findRandomQuestionsByLevelAndCategory(
                    blueprint.getLevel().getId(),
                    detail.getCategory().getName(),
                    PageRequest.of(0, detail.getQuestionCount())
            );

            if (questions.size() < detail.getQuestionCount()) {
                throw new CustomException(ErrorCode.NOT_ENOUGH_QUESTIONS);
            }

            for (Question q : questions) {
                ExamQuestion examQuestion = new ExamQuestion(exam, q, currentQuestionNumber++);
                examQuestions.add(examQuestion);
            }
        }

        examQuestionRepository.saveAll(examQuestions);

        return ExamTakingResponse.of(exam, examQuestions);
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
}
