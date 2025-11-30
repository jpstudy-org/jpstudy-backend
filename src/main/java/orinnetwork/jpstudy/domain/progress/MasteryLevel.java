package orinnetwork.jpstudy.domain.progress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MasteryLevel {
    NEW("신규"),
    APPRENTICE("학습 중"),
    GURU("익숙함"),
    MASTERED("마스터");

    private final String description;
}