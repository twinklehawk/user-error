CREATE TABLE users (
	id BIGSERIAL PRIMARY KEY,
	username VARCHAR(128) UNIQUE NOT NULL,
	password VARCHAR(512) NOT NULL
);
CREATE INDEX users_username_idx ON users (username);


CREATE TABLE roles (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(64) UNIQUE NOT NULL
);
CREATE INDEX roles_name_idx ON roles (name);

-- add initial user
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$BOTzy5.IGWm2Y./TTQ9jm.nDwGhyP0aryzBJGK9ODD96pUEAVYZu.');
