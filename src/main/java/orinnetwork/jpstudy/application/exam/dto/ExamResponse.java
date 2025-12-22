package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.application.admin.question.dto.QuestionResponse;
import orinnetwork.jpstudy.domain.exam.Exam;

@Schema(description = "시험 Blueprint 상세 조회 응답 DTO (문제 상세 정보 포함)")
public record ExamResponse(
        @Schema(description = "시험 Blueprint의 고유 ID")
        Long examId,

        @Schema(description = "시험 Blueprint의 제목")
        String title,

        @Schema(description = "시험 Blueprint의 난이도 레벨 이름")
        String levelName,

        @Schema(description = "시험의 총 응시 가능 시간 (분 단위)")
        int totalTimeMinutes,

        @Schema(description = "시험에 포함된 문제의 상세 정보 목록 (관리자용)")
        List<QuestionResponse> questions
) {
    public static ExamResponse of(Exam exam, List<QuestionResponse> questions) {
        return new ExamResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getLevel().getName(),
                exam.getTotalTimeMinutes(),
                questions
        );
    }
}
