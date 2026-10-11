-- liquibase formatted sql

-- changeset oskar:1791728532245-1
ALTER TABLE neene_user
    ADD profile_visibility VARCHAR(255);

-- changeset oskar:1791728532245-2
ALTER TABLE neene_user
    ALTER COLUMN profile_visibility SET NOT NULL;

-- changeset oskar:1791728532245-3
ALTER TABLE cross_stitch_pattern
    ADD visibility VARCHAR(255);

-- changeset oskar:1791728532245-4
ALTER TABLE cross_stitch_pattern
    ALTER COLUMN visibility SET NOT NULL;

-- changeset oskar:1791728532245-5
ALTER TABLE cross_stitch_pattern
    DROP COLUMN is_public;

