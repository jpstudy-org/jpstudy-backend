package orinnetwork.jpstudy.application.exam.dto;

import java.time.LocalDateTime;
import orinnetwork.jpstudy.domain.exam.TestAttempt;

public record StartTestResponse(
        Long attemptId,
        LocalDateTime startTime
) {
    public static StartTestResponse of(TestAttempt attempt) {
        return new StartTestResponse(attempt.getId(), attempt.getStartTime());
    }
}