package orinnetwork.jpstudy.domain.progress;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MasteryLevel {
    NEW("신규", Duration.ofMillis(10)), // 10분
    STEP_1("1단계", Duration.ofDays(1)),   // 1일
    STEP_2("2단계", Duration.ofDays(3)),   // 3일
    STEP_3("3단계", Duration.ofDays(7)),   // 7일
    STEP_4("4단계", Duration.ofDays(16)),  // 16일
    STEP_5("5단계", Duration.ofDays(35)),  // 35일
    MASTERED("마스터", Duration.ofDays(9999));

    private final String description;
    private final Duration baseInterval;

    public MasteryLevel getNextLevel() {
        if (this == MASTERED) {
            return MASTERED;
        }
        return values()[this.ordinal() + 1];
    }

    public MasteryLevel getPreviousLevel() {
        if (this == NEW) {
            return NEW;
        }
        return values()[this.ordinal() - 1];
    }
}