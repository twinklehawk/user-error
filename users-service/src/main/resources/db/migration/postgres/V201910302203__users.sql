SET search_path TO ${schema};

CREATE USER ${username} PASSWORD '${password}';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA ${schema} TO ${username};

-- data tables
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(128) NOT NULL,
    password VARCHAR(512) NOT NULL
);
CREATE INDEX users_username_idx ON users (username);
ALTER TABLE users ADD CONSTRAINT users_unique_username_idx UNIQUE (username);

CREATE TABLE applications
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);
CREATE INDEX applications_name_idx ON applications (name);
ALTER TABLE applications ADD CONSTRAINT applications_unique_name_idx UNIQUE (name);

CREATE TABLE roles
(
    id             BIGSERIAL PRIMARY KEY,
    application_id BIGINT      NOT NULL REFERENCES applications,
    name           VARCHAR(64) NOT NULL
);
CREATE INDEX roles_app_idx ON roles (application_id);
CREATE INDEX roles_name_idx ON roles (application_id, name);
ALTER TABLE roles ADD CONSTRAINT roles_unique_name_idx UNIQUE (application_id, name);

CREATE TABLE groups
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);
CREATE INDEX groups_name_idx ON groups (name);
ALTER TABLE groups ADD CONSTRAINT groups_unique_name_idx UNIQUE (name);

-- relation tables
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES users,
    role_id BIGINT NOT NULL REFERENCES roles
);
CREATE INDEX user_roles_user_id_idx ON user_roles (user_id);
CREATE INDEX user_roles_role_id_idx ON user_roles (role_id);

CREATE TABLE group_roles
(
    group_id BIGINT NOT NULL REFERENCES groups,
    role_id  BIGINT NOT NULL REFERENCES roles
);
CREATE INDEX group_roles_group_id_idx ON group_roles (group_id);
CREATE INDEX group_roles_role_id_idx ON group_roles (role_id);

CREATE TABLE user_groups
(
    user_id  BIGINT NOT NULL REFERENCES users,
    group_id BIGINT NOT NULL REFERENCES groups
);
CREATE INDEX user_groups_user_id_idx ON user_groups (user_id);
CREATE INDEX user_groups_group_id_idx ON user_groups (group_id);

-- add initial roles
INSERT INTO applications (name) VALUES ('bad-users');
INSERT INTO roles (application_id, name) VALUES ((select id from applications), 'bad-users-user');
INSERT INTO roles (application_id, name) VALUES ((select id from applications), 'bad-users-admin');
INSERT INTO groups (name) VALUES ('bad-users-admin');
INSERT INTO group_roles (group_id, role_id) SELECT groups.id, roles.id FROM groups, roles WHERE groups.name = 'bad-users-admin';

-- add initial user
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$BOTzy5.IGWm2Y./TTQ9jm.nDwGhyP0aryzBJGK9ODD96pUEAVYZu.');
INSERT INTO user_groups (user_id, group_id) SELECT users.id, groups.id FROM users, groups WHERE username = 'admin';
