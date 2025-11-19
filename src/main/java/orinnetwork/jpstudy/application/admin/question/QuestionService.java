package orinnetwork.jpstudy.application.admin.question;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.question.dto.QuestionResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategoryRepository;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.presentation.admin.question.dto.CreateQuestionRequest;
import orinnetwork.jpstudy.presentation.admin.question.dto.UpdateQuestionRequest;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final LevelRepository levelRepository;
    private final QuestionCategoryRepository categoryRepository;

    /**
     * 새로운 문제 생성
     */
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.LEVEL_NOT_FOUND));
        QuestionCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Question question = Question.builder()
                .level(level)
                .category(category)
                .questionText(request.questionText())
                .passage(request.passage())
                .audioUrl(request.audioUrl())
                .explanation(request.explanation())
                .build();

        request.choices().forEach(choiceDto -> {
            Choice choice = Choice.builder()
                    .question(question)
                    .choiceText(choiceDto.choiceText())
                    .isCorrect(choiceDto.isCorrect())
                    .build();
            question.getChoices().add(choice);
        });

        Question savedQuestion = questionRepository.save(question);

        return QuestionResponse.fromEntity(savedQuestion);
    }

    @Transactional
    public List<QuestionResponse> createOrUpdateQuestionsFromCSV(List<CreateQuestionRequest> requests) {
        Set<String> reqQuestionTexts = requests.stream()
                .map(CreateQuestionRequest::questionText)
                .collect(Collectors.toSet());

        Set<Long> reqLevelIds = requests.stream()
                .map(CreateQuestionRequest::levelId)
                .collect(Collectors.toSet());

        Set<Long> reqCategoryIds = requests.stream()
                .map(CreateQuestionRequest::categoryId)
                .collect(Collectors.toSet());

        Set<String> existingQuestionTexts = questionRepository
                .findByQuestionTextIn(reqQuestionTexts).stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toSet());

        Map<Long, Level> levelMap = levelRepository.findAllById(reqLevelIds).stream()
                .collect(Collectors.toMap(Level::getId, Function.identity()));

        Map<Long, QuestionCategory> categoryMap = categoryRepository.findAllById(reqCategoryIds).stream()
                .collect(Collectors.toMap(QuestionCategory::getId, Function.identity()));


        List<Question> questionsToSave = new ArrayList<>();

        for (CreateQuestionRequest request : requests) {

            if (existingQuestionTexts.contains(request.questionText())) {
                continue;
            }

            Level level = levelMap.get(request.levelId());
            QuestionCategory category = categoryMap.get(request.categoryId());

            if (level == null || category == null) {
                continue;
            }

            Question question = Question.builder()
                    .level(level)
                    .category(category)
                    .questionText(request.questionText())
                    .passage(request.passage())
                    .audioUrl(request.audioUrl())
                    .explanation(request.explanation())
                    .build();

            request.choices().forEach(choiceDto -> {
                Choice choice = Choice.builder()
                        .question(question)
                        .choiceText(choiceDto.choiceText())
                        .isCorrect(choiceDto.isCorrect())
                        .build();
                question.getChoices().add(choice);
            });

            questionsToSave.add(question);
        }

        List<Question> savedQuestions = questionRepository.saveAll(questionsToSave);

        return savedQuestions.stream()
                .map(QuestionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomPageResponse<QuestionResponse> getQuestions(Pageable pageable) {
        Page<Question> questionPage;

        questionPage = questionRepository.findAll(pageable);

        Page<QuestionResponse> response = questionPage.map(QuestionResponse::fromEntity);
        return new CustomPageResponse<>(response);
    }

    // 문제 단건 조회
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionResponse.fromEntity(question);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request) {
        // 1. 수정할 Question 엔티티를 조회합니다.
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 2. 연관된 Level, Category 엔티티를 조회합니다.
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.LEVEL_NOT_FOUND));
        QuestionCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 3. Question 엔티티의 내용을 업데이트합니다. (엔티티 내부에 update 메서드를 만드는 것이 더 객체지향적입니다.)
        question.update(
                level,
                category,
                request.questionText(),
                request.passage(),
                request.audioUrl(),
                request.explanation()
        );

        question.getChoices().clear();
        request.choices().forEach(choiceDto -> {
            Choice choice = Choice.builder()
                    .question(question)
                    .choiceText(choiceDto.getChoiceText())
                    .isCorrect(choiceDto.isCorrect())
                    .build();
            question.getChoices().add(choice);
        });

        return QuestionResponse.fromEntity(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        questionRepository.delete(question);
    }
}