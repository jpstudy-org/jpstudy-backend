ALTER TABLE member_kanji_progress
    ADD mastery_level VARCHAR(255);

ALTER TABLE member_kanji_progress
    ALTER COLUMN mastery_level SET NOT NULL;

ALTER TABLE member_word_progress
    ADD mastery_level VARCHAR(255);

ALTER TABLE member_word_progress
    ALTER COLUMN mastery_level SET NOT NULL;

ALTER TABLE member_kanji_progress
    DROP COLUMN status;

ALTER TABLE member_word_progress
    DROP COLUMN status;