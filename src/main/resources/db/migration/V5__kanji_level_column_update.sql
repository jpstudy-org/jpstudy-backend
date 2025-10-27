ALTER TABLE kanji
    ADD level INTEGER;

ALTER TABLE kanji
    DROP COLUMN jlpt_level;