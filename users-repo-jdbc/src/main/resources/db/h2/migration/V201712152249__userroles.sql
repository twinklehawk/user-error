CREATE TABLE user_roles (
	user_id BIGINT,
	role_id BIGINT
);

-- add initial roles
INSERT INTO ROLES (name) VALUES ('notes-user');
INSERT INTO ROLES (name) VALUES ('notes-admin');

-- add roles for initial admin user
INSERT INTO user_roles (user_id, role_id) VALUES (SELECT id FROM users WHERE username = 'admin', SELECT id FROM roles WHERE name = 'notes-user');
INSERT INTO user_roles (user_id, role_id) VALUES (SELECT id FROM users WHERE username = 'admin', SELECT id FROM roles WHERE name = 'notes-admin');