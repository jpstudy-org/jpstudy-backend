package orinnetwork.jpstudy.application.exam.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;
import orinnetwork.jpstudy.domain.exam.MemberAnswerRepository;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.ChoiceRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class TestAnswerManager {

    private final MemberAnswerRepository memberAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    @Transactional
    public void upsertAnswer(TestAttempt attempt, Long questionId, Long choiceId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Choice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new IllegalArgumentException("Choice not found"));

        memberAnswerRepository.findByTestAttemptAndQuestion(attempt, question)
                .ifPresentOrElse(
                        existing -> existing.changeChoice(choice),
                        () -> createNewAnswer(attempt, question, choice)
                );
    }

    private void createNewAnswer(TestAttempt attempt, Question question, Choice choice) {
        memberAnswerRepository.save(MemberAnswer.builder()
                .testAttempt(attempt)
                .question(question)
                .selectedChoice(choice)
                .isCorrect(false)
                .build());
    }
}
