CREATE TABLE user_note_permissions (
	user_id BIGINT,
	note_id BIGINT,
	readable BOOLEAN,
	writable BOOLEAN
);

CREATE INDEX ON user_note_permissions(user_id);
CREATE INDEX ON user_note_permissions(user_id, note_id);