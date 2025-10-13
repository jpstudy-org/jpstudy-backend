package orinnetwork.jpstudy.application.question;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.question.dto.CreateQuestionRequest;
import orinnetwork.jpstudy.application.question.dto.QuestionResponseDto;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategoryRepository;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.presentation.question.dto.UpdateQuestionRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final LevelRepository levelRepository;
    private final QuestionCategoryRepository categoryRepository;

    /**
     * 새로운 문제 생성 : 쓰기 작업 필요
     */
    @Transactional
    public QuestionResponseDto createQuestion(CreateQuestionRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨입니다."));
        QuestionCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Question question = Question.builder()
                .level(level)
                .category(category)
                .questionText(request.questionText())
                .explanation(request.explanation())
                .build();

        request.choices().forEach(choiceDto -> {
            Choice choice = Choice.builder()
                    .question(question)
                    .choiceText(choiceDto.text())
                    .isCorrect(choiceDto.isCorrect())
                    .build();
            question.getChoices().add(choice);
        });

        Question savedQuestion = questionRepository.save(question);

        return QuestionResponseDto.fromEntity(savedQuestion);
    }

    public QuestionResponseDto getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        return QuestionResponseDto.fromEntity(question);
    }

    @Transactional
    public QuestionResponseDto updateQuestion(Long questionId, UpdateQuestionRequest request) {
        // 1. 수정할 Question 엔티티를 조회합니다.
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다. ID: " + questionId));

        // 2. 연관된 Level, Category 엔티티를 조회합니다.
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레벨입니다."));
        QuestionCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 3. Question 엔티티의 내용을 업데이트합니다. (엔티티 내부에 update 메서드를 만드는 것이 더 객체지향적입니다.)
        question.update(
                level,
                category,
                request.questionText(),
                request.passage(),
                request.audioUrl(),
                request.explanation()
        );

        // 4. 선택지(Choices)를 업데이트합니다.
        //    가장 간단한 방법은 기존 선택지를 모두 지우고 새로 추가하는 것입니다.
        question.getChoices().clear();
        request.choices().forEach(choiceDto -> {
            Choice choice = Choice.builder()
                    .question(question)
                    .choiceText(choiceDto.choiceText())
                    .isCorrect(choiceDto.isCorrect())
                    .build();
            question.getChoices().add(choice);
        });

        return QuestionResponseDto.fromEntity(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다. ID: " + questionId));

        questionRepository.delete(question);
    }
}