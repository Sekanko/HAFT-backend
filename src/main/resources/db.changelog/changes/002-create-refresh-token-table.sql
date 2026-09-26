-- liquibase formatted sql

-- changeset oskar:1790460453586-1
CREATE TABLE refresh_token
(
    id            UUID                           NOT NULL,
    token         VARCHAR(255),
    neene_user_id UUID,
    token_status  VARCHAR(255),
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    expires_at    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

-- changeset oskar:1790460453586-2
ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_NEENE_USER FOREIGN KEY (neene_user_id) REFERENCES neene_user (id);

