CREATE TABLE user_roles (
	user_id BIGINT NOT NULL REFERENCES users,
	role_id BIGINT NOT NULL REFERENCES roles
);
CREATE INDEX user_roles_user_id_idx ON user_roles (user_id);
CREATE INDEX user_roles_role_id_idx ON user_roles (role_id);

-- add initial roles
INSERT INTO roles (name) VALUES ('users-user');
INSERT INTO roles (name) VALUES ('users-admin');

-- add roles for initial admin user
INSERT INTO user_roles (user_id, role_id) SELECT users.id, roles.id FROM users, roles WHERE username = 'admin';
