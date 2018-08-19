ALTER TABLE notes DROP COLUMN owner_id;

ALTER TABLE user_note_permissions ADD COLUMN (
    owner BOOLEAN
) AFTER writable;
