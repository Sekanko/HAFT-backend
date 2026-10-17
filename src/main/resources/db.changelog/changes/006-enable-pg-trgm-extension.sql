-- liquibase formatted sql

-- changeset oskar:1792256087591-1
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- changeset oskar:1792256087591-2
CREATE INDEX idx_users_name_trgm
  ON neene_user USING gin (lower(first_name || ' ' || last_name) gin_trgm_ops);