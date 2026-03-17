package orinnetwork.jpstudy.application.progress.word.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "단어 학습 세션 응답 DTO (복습 카드와 새 카드를 포함)")
public class StudySessionResponse {

    @Schema(description = "학습 세션 ID")
    private final Long sessionId;

    @Schema(description = "이번 세션에 복습할 단어 카드 목록")
    private final List<WordCard> reviewCards;

    @Schema(description = "이번 세션에 새로 학습할 단어 카드 목록 (미학습 항목)")
    private final List<WordCard> newCards;

    @Schema(description = "세션 내 총 학습 항목 수")
    private final int totalCount;

    @Schema(description = "세션 내 학습 완료한 항목 수")
    private final long studiedCount;

    @Schema(description = "현재 세션 완료 여부 (모든 항목 학습 시 true)")
    private final boolean completed;

    @Schema(description = "더 학습 가능한 단어가 남아있는지 여부")
    private final boolean hasMoreItems;

    public StudySessionResponse(Long sessionId, List<WordCard> reviewCards, List<WordCard> newCards,
                                int totalCount, long studiedCount, boolean completed, boolean hasMoreItems) {
        this.sessionId = sessionId;
        this.reviewCards = reviewCards;
        this.newCards = newCards;
        this.totalCount = totalCount;
        this.studiedCount = studiedCount;
        this.completed = completed;
        this.hasMoreItems = hasMoreItems;
    }
}
