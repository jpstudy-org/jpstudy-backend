UPDATE member_kanji_progress
SET mastery_level = 'NEW',
    stability = 0.0,
    difficulty = 0.0,
    last_reviewed_at = NULL,
    next_review_at = NOW();

UPDATE member_word_progress
SET mastery_level = 'NEW',
    stability = 0.0,
    difficulty = 0.0,
    last_reviewed_at = NULL,
    next_review_at = NOW();