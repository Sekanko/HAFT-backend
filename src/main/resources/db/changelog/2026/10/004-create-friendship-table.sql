-- liquibase formatted sql

-- changeset oskar:1791714802405-1
CREATE TABLE friendship
(
    id          UUID                           NOT NULL,
    receiver_id UUID,
    sender_id   UUID,
    status      VARCHAR(255),
    creation_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    response_at TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT pk_friendship PRIMARY KEY (id)
);

-- changeset oskar:1791714802405-2
ALTER TABLE friendship
    ADD CONSTRAINT FK_FRIENDSHIP_ON_RECEIVER FOREIGN KEY (receiver_id) REFERENCES neene_user (id);

-- changeset oskar:1791714802405-3
ALTER TABLE friendship
    ADD CONSTRAINT FK_FRIENDSHIP_ON_SENDER FOREIGN KEY (sender_id) REFERENCES neene_user (id);

-- changeset oskar:1791714802405-4
ALTER TABLE friendship
    ADD CONSTRAINT chk_friendships_not_self CHECK (sender_id <> receiver_id);

-- changeset oskar:1791714802405-5
CREATE UNIQUE INDEX  unique_friendship_pair on friendship (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id));

-- changeset oskar:1791714802405-6
CREATE INDEX idx_friendship_sender on friendship (sender_id);
CREATE INDEX idx_friendship_receiver on friendship (receiver_id);