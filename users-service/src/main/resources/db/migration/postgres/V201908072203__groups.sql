SET search_path TO ${schema};

CREATE TABLE groups (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL
);
ALTER TABLE groups ADD CONSTRAINT groups_unique_name_con UNIQUE (name);
CREATE INDEX groups_name_idx ON groups (name);

CREATE TABLE group_roles (
  group_id BIGINT NOT NULL REFERENCES groups,
  role_id BIGINT NOT NULL REFERENCES roles
);
CREATE INDEX group_roles_group_id_idx ON group_roles (group_id);

CREATE TABLE user_groups (
  user_id BIGINT NOT NULL REFERENCES users,
  group_id BIGINT NOT NULL REFERENCES groups
);
CREATE INDEX user_groups_user_id_idx ON user_groups (user_id);
