package orinnetwork.jpstudy.application.progress.common.dto;

import java.time.LocalDateTime;

public record ReviewResult(
        Double newStability,
        Double newDifficulty,
        LocalDateTime reviewedAt,
        LocalDateTime nextReviewAt
) {
}