ALTER TABLE post
    ADD comment_count INTEGER DEFAULT 0;

ALTER TABLE post
    ADD ip_address VARCHAR(50);

ALTER TABLE post
    ADD post_status VARCHAR(255);

ALTER TABLE post
    ADD view_count INTEGER DEFAULT 0;

ALTER TABLE post
    ALTER COLUMN comment_count SET NOT NULL;

ALTER TABLE comment
    ADD comment_status VARCHAR(255);

ALTER TABLE comment
    ADD ip_address VARCHAR(50);

ALTER TABLE comment
    ALTER COLUMN comment_status SET NOT NULL;

ALTER TABLE post
    ALTER COLUMN post_status SET NOT NULL;

ALTER TABLE post
    ALTER COLUMN view_count SET NOT NULL;