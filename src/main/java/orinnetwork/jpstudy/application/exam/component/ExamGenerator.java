package orinnetwork.jpstudy.application.exam.component;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.domain.exam.BlueprintDetail;
import orinnetwork.jpstudy.domain.exam.Exam;
import orinnetwork.jpstudy.domain.exam.ExamBlueprint;
import orinnetwork.jpstudy.domain.exam.ExamBlueprintRepository;
import orinnetwork.jpstudy.domain.exam.ExamQuestion;
import orinnetwork.jpstudy.domain.exam.ExamQuestionRepository;
import orinnetwork.jpstudy.domain.exam.ExamRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class ExamGenerator {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamBlueprintRepository examBlueprintRepository;

    @Transactional
    public ExamTakingResponse generate(Long blueprintId, String title) {
        ExamBlueprint blueprint = getBlueprint(blueprintId);
        Exam exam = createAndSaveExam(blueprint, title);

        List<ExamQuestion> examQuestions = generateQuestions(blueprint, exam);

        examQuestionRepository.saveAll(examQuestions);

        return ExamTakingResponse.of(exam, examQuestions);
    }

    private ExamBlueprint getBlueprint(Long id) {
        return examBlueprintRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));
    }

    private Exam createAndSaveExam(ExamBlueprint blueprint, String title) {
        Exam exam = Exam.builder()
                .level(blueprint.getLevel())
                .title(title)
                .totalTimeMinutes(blueprint.getTotalTimeMinutes())
                .build();
        return examRepository.save(exam);
    }

    private List<ExamQuestion> generateQuestions(ExamBlueprint blueprint, Exam exam) {
        List<ExamQuestion> examQuestions = new ArrayList<>();
        int currentNumber = 1;

        for (BlueprintDetail detail : blueprint.getDetails()) {
            currentNumber = addQuestionsForDetail(detail, exam, examQuestions, currentNumber);
        }
        return examQuestions;
    }

    private int addQuestionsForDetail(BlueprintDetail detail, Exam exam, List<ExamQuestion> destList, int startNum) {
        List<Question> questions = fetchRandomQuestions(detail);

        for (Question q : questions) {
            destList.add(new ExamQuestion(exam, q, startNum++));
        }
        return startNum;
    }

    private List<Question> fetchRandomQuestions(BlueprintDetail detail) {
        List<Question> questions = questionRepository.findRandomQuestionsByLevelAndCategory(
                detail.getBlueprint().getLevel().getId(),
                detail.getCategory().getName(),
                PageRequest.of(0, detail.getQuestionCount())
        );

        if (questions.size() < detail.getQuestionCount()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_QUESTIONS);
        }
        return questions;
    }
}
