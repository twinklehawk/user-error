CREATE TABLE users (
	id BIGSERIAL PRIMARY KEY,
	username VARCHAR(128),
	password VARCHAR(512)
);

CREATE TABLE roles (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(64)
);

-- add initial user
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$BOTzy5.IGWm2Y./TTQ9jm.nDwGhyP0aryzBJGK9ODD96pUEAVYZu.');
