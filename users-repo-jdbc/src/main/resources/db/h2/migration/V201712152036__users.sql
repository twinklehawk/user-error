CREATE TABLE users (
	id IDENTITY PRIMARY KEY,
	username VARCHAR(128),
	password VARCHAR(512)
);

CREATE TABLE roles (
	id IDENTITY PRIMARY KEY,
	name VARCHAR(32)
);

-- add initial user
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$BOTzy5.IGWm2Y./TTQ9jm.nDwGhyP0aryzBJGK9ODD96pUEAVYZu.');