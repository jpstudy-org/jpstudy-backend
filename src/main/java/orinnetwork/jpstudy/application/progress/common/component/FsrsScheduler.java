package orinnetwork.jpstudy.application.progress.common.component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResult;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.progress.MemberKanjiProgress;
import orinnetwork.jpstudy.domain.progress.MemberWordProgress;
import orinnetwork.jpstudy.domain.word.Word;

@Component
public class FsrsScheduler {
    // 표준 가중치 (Default Weights)
    private static final double[] w = {
            0.40255, 1.18385, 3.173, 15.69105,
            7.1960, 0.5345, 1.4604, 0.0046, 1.54575, 0.1192, 1.01925,
            1.9395, 0.11, 2.9605, 0.5136, 0.942, 0.8803, 0.4166, 0.5117
    };

    private static final double REQUEST_RETENTION = 0.9; // 목표 기억률 90%
    private static final double DECAY = -0.5;
    private static final double FACTOR = 0.9; // (19/20)
    private static final long AGAIN_INTERVAL_MINUTES = 10; // 'Again' 선택 시 10분 뒤 복습

    // ------------------------------------------------------------------------
    // Public Methods (Kanji & Word Support)
    // ------------------------------------------------------------------------

    /**
     * [Kanji] 다음 복습 스케줄 계산
     */
    public ReviewResult calculateNextReview(MemberKanjiProgress progress, Kanji kanji, ReviewDifficulty difficulty) {
        LocalDateTime now = LocalDateTime.now();
        int rating = mapToRating(difficulty);

        // 첫 학습(New)인 경우
        if (progress.getStability() == 0.0) {
            return calculateInitial(now, rating, kanji.getLevel());
        }

        // 복습(Review)인 경우
        return calculateReview(
                progress.getStability(),
                progress.getDifficulty(),
                progress.getLastReviewedAt(),
                now,
                rating
        );
    }

    /**
     * [Word] 다음 복습 스케줄 계산 (Overloading)
     */
    public ReviewResult calculateNextReview(MemberWordProgress progress, Word word, ReviewDifficulty difficulty) {
        LocalDateTime now = LocalDateTime.now();
        int rating = mapToRating(difficulty);

        // 첫 학습(New)인 경우
        if (progress.getStability() == 0.0) {
            return calculateInitial(now, rating, word.getLevel());
        }

        // 복습(Review)인 경우
        return calculateReview(
                progress.getStability(),
                progress.getDifficulty(),
                progress.getLastReviewedAt(),
                now,
                rating
        );
    }

    // ------------------------------------------------------------------------
    // Core Logic (Shared)
    // ------------------------------------------------------------------------

    /**
     * 초기 학습 계산 (Initial Learning)
     * DB 레벨(100~1)을 FSRS 난이도(1~10)로 변환하여 초기값 설정
     */
    private ReviewResult calculateInitial(LocalDateTime now, int rating, int dbEntityLevel) {
        double intrinsicD = (100.0 - dbEntityLevel) / 10.0 + 1.0;

        double baseD = w[4] - (rating - 3) * w[5];

        double newD = constrain((baseD + intrinsicD) / 2.0);

        double newS = w[rating - 1];

        return new ReviewResult(newS, newD, now, calculateDate(now, newS));
    }

    /**
     * 반복 학습 계산 (Review)
     * FSRS v5 공식 적용
     */
    private ReviewResult calculateReview(double s, double d, LocalDateTime lastReviewed, LocalDateTime now, int rating) {
        long elapsedDays = Math.max(0, ChronoUnit.DAYS.between(lastReviewed, now));

        // 현재 기억 확률 계산
        double r = Math.pow(1 + FACTOR * elapsedDays / s, DECAY);

        // --- Next Difficulty ---
        double nextD = d - w[6] * (rating - 3);
        nextD = w[7] * w[4] + (1 - w[7]) * nextD; // Mean Reversion (평균으로 회귀)
        nextD = constrain(nextD);

        // --- Next Stability ---
        double nextS;
        if (rating == 1) {
            nextS = w[11] *
                    Math.pow(d, -w[12]) *
                    (Math.pow(s + 1, w[13]) - 1) *
                    Math.pow(Math.E, w[14] * (1 - r));
        } else {
            double hardPenalty = (rating == 2) ? w[15] : 1;
            double easyBonus = (rating == 4) ? w[16] : 1;

            nextS = s * (1 + Math.pow(Math.E, w[8]) *
                    (11 - d) *
                    Math.pow(s, -w[9]) *
                    (Math.pow(Math.E, w[10] * (1 - r)) - 1) *
                    hardPenalty *
                    easyBonus);
        }

        return new ReviewResult(nextS, nextD, now, calculateDate(now, nextS));
    }

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    private LocalDateTime calculateDate(LocalDateTime now, double stability) {
        long nextIntervalDays = (long) (stability * 9 * (1 / REQUEST_RETENTION - 1));

        if (nextIntervalDays < 1) {
            return now.plusMinutes(AGAIN_INTERVAL_MINUTES);
        }

        return now.plusDays(nextIntervalDays);
    }

    private double constrain(double d) {
        return Math.min(Math.max(d, 1.0), 10.0);
    }

    private int mapToRating(ReviewDifficulty difficulty) {
        return switch (difficulty) {
            case AGAIN -> 1;
            case HARD -> 2;
            case GOOD -> 3;
            case EASY -> 4;
        };
    }
}