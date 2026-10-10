-- liquibase formatted sql

-- changeset oskar:1791038331305-1
CREATE TABLE cross_stitch_pattern
(
    id             UUID                           NOT NULL,
    creator_id     UUID                           NOT NULL,
    title          VARCHAR(255)                   NOT NULL,
    is_public      BOOLEAN                        NOT NULL,
    canvas_width   INTEGER                        NOT NULL,
    canvas_height  INTEGER                        NOT NULL,
    canvas_density INTEGER                        NOT NULL,
    colors_count   INTEGER                        NOT NULL,
    pattern_key    VARCHAR(512),
    thumbnail_key    VARCHAR(512),
    created_at     TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_cross_stitch_pattern PRIMARY KEY (id)
);

-- changeset oskar:1791038331305-2
ALTER TABLE cross_stitch_pattern
    ADD CONSTRAINT FK_CROSS_STITCH_PATTERN_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES neene_user (id);
