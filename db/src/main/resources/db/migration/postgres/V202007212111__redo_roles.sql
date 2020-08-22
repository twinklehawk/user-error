SET search_path TO ${schema};

DELETE FROM roles;

INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'validate-tokens');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'view-applications');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'edit-applications');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'view-roles');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'edit-roles');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'view-groups');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'edit-groups');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'view-users');
INSERT INTO roles (application_id, name) VALUES ((select id from applications where name = 'user-error'), 'edit-users');

-- grant all roles to admin group
INSERT INTO group_roles (group_id, role_id) SELECT groups.id, roles.id FROM groups, roles WHERE groups.name = 'user-error-admin';
