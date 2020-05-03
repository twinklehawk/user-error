SET search_path TO ${schema};

ALTER TABLE user_auth_settings ADD COLUMN auth_token_expiration BIGINT;
ALTER TABLE user_auth_settings ADD COLUMN refresh_token_expiration BIGINT;
