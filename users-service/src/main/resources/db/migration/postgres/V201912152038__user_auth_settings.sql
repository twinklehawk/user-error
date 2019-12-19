SET search_path TO ${schema};

CREATE TABLE IF NOT EXISTS user_auth_settings
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
    refresh_token_enabled BOOLEAN NOT NULL
);
CREATE INDEX user_auth_settings_user_id_idx ON user_auth_settings (user_id);
ALTER TABLE user_auth_settings ADD CONSTRAINT user_auth_settings_unique_user_id_idx UNIQUE (user_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON user_auth_settings TO ${username};
GRANT USAGE ON user_auth_settings_id_seq TO ${username};
