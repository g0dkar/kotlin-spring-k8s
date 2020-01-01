-- Setup Liquibase Tables
DROP TABLE IF EXISTS databasechangeloglock;

CREATE TABLE databasechangeloglock (
  ID INTEGER NOT NULL,
  LOCKED BOOLEAN NOT NULL,
  LOCKGRANTED TIMESTAMP WITHOUT TIME ZONE,
  LOCKEDBY VARCHAR(255),
  CONSTRAINT DATABASECHANGELOGLOCK_PKEY PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS databasechangelog;

CREATE TABLE databasechangelog (
  ID VARCHAR(255) NOT NULL,
  AUTHOR VARCHAR(255) NOT NULL,
  FILENAME VARCHAR(255) NOT NULL,
  DATEEXECUTED TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  ORDEREXECUTED INTEGER NOT NULL,
  EXECTYPE VARCHAR(10) NOT NULL,
  MD5SUM VARCHAR(35),
  DESCRIPTION VARCHAR(255),
  COMMENTS VARCHAR(255),
  TAG VARCHAR(255),
  LIQUIBASE VARCHAR(20),
  CONTEXTS VARCHAR(255),
  LABELS VARCHAR(255),
  DEPLOYMENT_ID VARCHAR(10)
);



-- --------------------------------------------------- --
-- Trigger to set the `created` and `updated` fields
CREATE OR REPLACE FUNCTION public.set_created_updated_timestamps()
RETURNS trigger AS $function$
BEGIN
  BEGIN
    NEW.updated := clock_timestamp();
    EXCEPTION WHEN UNDEFINED_COLUMN THEN END;

  -- Set only if the row is being created
  IF NEW.created IS NULL THEN
    -- Set or overwrite creation timestamp
    NEW.created := clock_timestamp();
  END IF;

  RETURN NEW;
END $function$ LANGUAGE plpgsql;



-- --------------------------------------------------- --
-- Trigger turn tables without `updated` into append-only
CREATE OR REPLACE FUNCTION public.update_timestamps_and_append_only()
RETURNS trigger AS $function$
BEGIN
  -- `created` cannot be updated
  -- (will be updated automatically by the previous trigger, thus throwing here if anything changed)
  IF NEW.created IS DISTINCT FROM OLD.created THEN
    RAISE EXCEPTION 'The creation timestamp cannot be modified.';
  END IF;

  BEGIN
    NEW.updated := clock_timestamp();

    -- The table does not have an `updated` column
    EXCEPTION WHEN undefined_column THEN
    RAISE EXCEPTION 'Cannot update append-only table %.%!', TG_TABLE_SCHEMA, TG_TABLE_NAME;
  END;

  RETURN NEW;

END $function$ LANGUAGE plpgsql;



-- --------------------------------------------------- --
-- Add the previous triggers to a table when creating it
CREATE OR REPLACE FUNCTION public.add_triggers_to_table(schema_qualified_tablename text)
RETURNS void AS $function$
DECLARE
  schema_name text;
  table_name text;
  q_txt text;
BEGIN
  SELECT
    (parse_ident(schema_qualified_tablename))[1],
    (parse_ident(schema_qualified_tablename))[2]
  INTO schema_name, table_name;

  -- Don't apply this to the Liquibase Tables
  IF table_name NOT IN ('databasechangeloglock', 'databasechangelog') THEN
    EXECUTE format(
      $SQL$
          DROP TRIGGER IF EXISTS set_created_updated_timestamps_trigger ON %I.%I;
      $SQL$,
      schema_name, table_name);

    q_txt := format(
      $SQL$
          CREATE TRIGGER set_created_updated_timestamps_trigger
          BEFORE INSERT ON %I.%I
          FOR EACH ROW
          EXECUTE PROCEDURE public.set_created_updated_timestamps();
      $SQL$,
      schema_name, table_name);

    RAISE NOTICE '%', q_txt;

    EXECUTE q_txt;

    EXECUTE format(
      $SQL$
          DROP TRIGGER IF EXISTS update_timestamps_and_append_only_trigger ON %I.%I;
      $SQL$,
      schema_name, table_name);

    q_txt := format(
      $SQL$
          CREATE TRIGGER update_timestamps_and_append_only_trigger
          BEFORE UPDATE ON %I.%I
          FOR EACH ROW
          EXECUTE PROCEDURE public.update_timestamps_and_append_only();
      $SQL$,
      schema_name, table_name);

    RAISE NOTICE '%', q_txt;

    EXECUTE q_txt;
  END IF;
END $function$ LANGUAGE plpgsql;



-- --------------------------------------------------- --
-- Auto-configure triggers when creating tables
CREATE OR REPLACE FUNCTION public.add_triggers_to_new_tables()
RETURNS event_trigger
LANGUAGE 'plpgsql'
COST 100
VOLATILE NOT LEAKPROOF
AS $function$

DECLARE
  rec RECORD;
  schema_name text;
  table_name text;

BEGIN
  FOR rec IN SELECT * FROM pg_event_trigger_ddl_commands() LOOP
    IF rec.command_tag = 'CREATE TABLE' AND upper(rec.object_type) = 'TABLE' THEN
      SELECT
        (parse_ident(rec.object_identity))[1],
        (parse_ident(rec.object_identity))[2]
      INTO schema_name, table_name;

      -- Ignore Temporary Tables
      IF schema_name != 'pg_temp' THEN
        PERFORM public.add_triggers_to_table(rec.object_identity);
      END IF;
    END IF;
  END LOOP;
END $function$;



-- --------------------------------------------------- --
-- Create the trigger to setup tables when they're created
DROP EVENT TRIGGER IF EXISTS configure_table_ddl_command_end_trigger;

CREATE EVENT TRIGGER configure_table_ddl_command_end_trigger
ON DDL_COMMAND_END
EXECUTE PROCEDURE public.add_triggers_to_new_tables();
