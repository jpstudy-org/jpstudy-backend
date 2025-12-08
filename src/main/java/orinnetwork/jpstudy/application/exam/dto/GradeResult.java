package orinnetwork.jpstudy.application.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;

@Schema(description = "시험 채점 결과 (서버 내부 또는 최종 응답 생성 시 사용)")
public record GradeResult(
        @Schema(description = "최종 점수")
        int score,

        @Schema(description = "개별 문제별 사용자 답안 및 정답 여부 결과 목록")
        List<MemberAnswer> answers
) {
}