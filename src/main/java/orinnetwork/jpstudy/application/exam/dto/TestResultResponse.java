package orinnetwork.jpstudy.application.exam.dto;

import java.time.LocalDateTime;
import java.util.List;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;

public record TestResultResponse(
        Long attemptId,
        Long examId,
        String examTitle,
        Integer score,
        int totalQuestions,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<MemberAnswer> results
) {
}
