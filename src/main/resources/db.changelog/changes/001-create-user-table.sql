-- liquibase formatted sql

-- changeset oskar:1790368642773-1
CREATE TABLE neene_user
(
    id            UUID                           NOT NULL,
    email         VARCHAR(255)                   NOT NULL,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    password_hash VARCHAR(255)                   NOT NULL,
    avatar_key    VARCHAR(512),
    system_role   VARCHAR(20)                    NOT NULL,
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_neene_user PRIMARY KEY (id)
);

-- changeset oskar:1790368642773-2
ALTER TABLE neene_user
    ADD CONSTRAINT uc_neene_user_email UNIQUE (email);

