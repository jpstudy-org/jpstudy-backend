ALTER TABLE inquiry
    ADD answer_content OID;

ALTER TABLE inquiry
    ADD answered_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE member
    ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;

ALTER TABLE member
    ADD COLUMN ban_expires_at TIMESTAMP;