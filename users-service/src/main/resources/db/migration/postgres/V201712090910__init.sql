CREATE TABLE notes (
	id BIGSERIAL PRIMARY KEY,
	owner_id BIGINT,
	correlation_id BIGINT,
	title VARCHAR(128),
	content VARCHAR(4096)
)
