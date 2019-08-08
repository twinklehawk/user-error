SET search_path TO ${schema};

DO $$
DECLARE
    con_name TEXT;
BEGIN
    SELECT constraint_name INTO con_name FROM information_schema.table_constraints
        WHERE table_schema = '${schema}' AND table_name = 'roles' AND constraint_type = 'UNIQUE';
    EXECUTE 'alter table ${schema}.roles drop constraint ' || con_name;
END;
$$;

ALTER TABLE roles ADD CONSTRAINT roles_unique_name_idx UNIQUE (name, application);
