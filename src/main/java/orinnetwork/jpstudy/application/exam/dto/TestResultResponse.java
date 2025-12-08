package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;

@Schema(description = "사용자의 시험 응시 결과 상세 응답 DTO")
public record TestResultResponse(
        @Schema(description = "응시 기록의 고유 ID")
        Long attemptId,

        @Schema(description = "응시한 시험의 Blueprint ID")
        Long examId,

        @Schema(description = "응시한 시험의 이름/제목")
        String examTitle,

        @Schema(description = "최종 점수")
        Integer score,

        @Schema(description = "총 문제 수")
        int totalQuestions,

        @Schema(description = "시험 시작 시각")
        LocalDateTime startTime,

        @Schema(description = "시험 종료 시각 (제출 시각)")
        LocalDateTime endTime,

        @Schema(description = "개별 문제별 사용자 답안 및 정답 여부 결과 목록")
        List<MemberAnswer> results
) {
}
