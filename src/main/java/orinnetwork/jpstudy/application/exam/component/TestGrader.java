package orinnetwork.jpstudy.application.exam.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.exam.dto.GradeResult;
import orinnetwork.jpstudy.application.exam.dto.UserAnswer;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;
import orinnetwork.jpstudy.domain.exam.TestAttempt;
import orinnetwork.jpstudy.domain.questionbank.Choice;
import orinnetwork.jpstudy.domain.questionbank.ChoiceRepository;
import orinnetwork.jpstudy.domain.questionbank.Question;
import orinnetwork.jpstudy.domain.questionbank.QuestionRepository;

@Component
@RequiredArgsConstructor
public class TestGrader {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    @Transactional(readOnly = true)
    public GradeResult grade(TestAttempt attempt, List<UserAnswer> userAnswers) {
        Map<Long, Question> questionMap = loadQuestions(userAnswers);
        Map<Long, Choice> choiceMap = loadChoices(userAnswers);

        List<MemberAnswer> gradedAnswers = new ArrayList<>();
        int score = 0;

        for (UserAnswer userAnswer : userAnswers) {
            boolean isCorrect = isAnswerCorrect(userAnswer, questionMap, choiceMap);
            if (isCorrect) score++;

            gradedAnswers.add(createMemberAnswer(attempt, userAnswer, questionMap, choiceMap, isCorrect));
        }

        return new GradeResult(score, gradedAnswers);
    }

    // --- Private Helper ---

    private Map<Long, Question> loadQuestions(List<UserAnswer> answers) {
        List<Long> ids = answers.stream().map(UserAnswer::questionId).toList();
        return questionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));
    }

    private Map<Long, Choice> loadChoices(List<UserAnswer> answers) {
        List<Long> ids = answers.stream().map(UserAnswer::selectedChoiceId).toList();
        return choiceRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Choice::getId, Function.identity()));
    }

    private boolean isAnswerCorrect(UserAnswer answer, Map<Long, Question> qMap, Map<Long, Choice> cMap) {
        Question question = qMap.get(answer.questionId());
        Choice choice = cMap.get(answer.selectedChoiceId());

        return question != null && choice != null
                && choice.getQuestion().getId().equals(question.getId())
                && choice.isCorrect();
    }

    private MemberAnswer createMemberAnswer(TestAttempt attempt, UserAnswer ua,
                                            Map<Long, Question> qMap, Map<Long, Choice> cMap,
                                            boolean isCorrect) {
        return MemberAnswer.builder()
                .testAttempt(attempt)
                .question(qMap.get(ua.questionId()))
                .selectedChoice(cMap.get(ua.selectedChoiceId()))
                .isCorrect(isCorrect)
                .build();
    }
}
