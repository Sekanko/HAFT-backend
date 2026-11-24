-- liquibase formatted sql

-- changeset oskartarka:1795514639454-1
CREATE TABLE chat_message
(
    uuid         UUID                        NOT NULL,
    chat_room_id UUID                        NOT NULL,
    sender_id    UUID                        NOT NULL,
    content      TEXT,
    image_key    VARCHAR(255),
    created_at   TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_chatmessage PRIMARY KEY (uuid)
);

-- changeset oskartarka:1795514639454-2
CREATE TABLE chat_participant
(
    chat_room_id  UUID                        NOT NULL,
    neene_user_id UUID                        NOT NULL,
    role          VARCHAR(20)                 NOT NULL,
    joined_at     TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    last_read_at  TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_chat_participant PRIMARY KEY (chat_room_id, neene_user_id)
);

-- changeset oskartarka:1795514639454-3
CREATE TABLE chat_room
(
    id              UUID                        NOT NULL,
    type            VARCHAR(255)                NOT NULL,
    name            VARCHAR(255),
    dm_key          VARCHAR(73),
    last_message_at TIMESTAMP(6) WITH TIME ZONE,
    created_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_chat_room PRIMARY KEY (id)
);

-- changeset oskartarka:1795514639454-4
ALTER TABLE chat_room
    ADD CONSTRAINT uc_chat_room_dmkey UNIQUE (dm_key);

-- changeset oskartarka:1795514639454-5
ALTER TABLE chat_message
    ADD CONSTRAINT FK_CHATMESSAGE_ON_CHATROOM FOREIGN KEY (chat_room_id) REFERENCES chat_room (id);

-- changeset oskartarka:1795514639454-6
ALTER TABLE chat_message
    ADD CONSTRAINT FK_CHATMESSAGE_ON_SENDER FOREIGN KEY (sender_id) REFERENCES neene_user (id);

-- changeset oskartarka:1795514639454-7
ALTER TABLE chat_participant
    ADD CONSTRAINT FK_CHAT_PARTICIPANT_ON_CHAT_ROOM FOREIGN KEY (chat_room_id) REFERENCES chat_room (id);

-- changeset oskartarka:1795514639454-8
ALTER TABLE chat_participant
    ADD CONSTRAINT FK_CHAT_PARTICIPANT_ON_NEENE_USER FOREIGN KEY (neene_user_id) REFERENCES neene_user (id);

