SET search_path TO ${schema};

ALTER TABLE roles ADD COLUMN application VARCHAR NOT NULL DEFAULT 'bad-users';
