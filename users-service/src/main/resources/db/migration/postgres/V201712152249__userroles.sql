CREATE TABLE user_roles (
	user_id BIGINT,
	role_id BIGINT
);

-- add initial roles
INSERT INTO ROLES (name) VALUES ('users-user');
INSERT INTO ROLES (name) VALUES ('users-admin');

-- add roles for initial admin user
INSERT INTO user_roles (user_id, role_id) SELECT users.id, roles.id FROM users, roles WHERE username = 'admin';
