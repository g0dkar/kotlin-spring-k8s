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
RETURNS trigger AS $$
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
END;

$$ LANGUAGE plpgsql;



-- --------------------------------------------------- --
-- Trigger turn tables without `updated` into append-only
CREATE OR REPLACE FUNCTION public.update_timestamps_and_append_only()
RETURNS trigger AS $$
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

END;
$$ LANGUAGE plpgsql;



-- --------------------------------------------------- --
-- When creating a table, add the previous triggers to it
CREATE OR REPLACE FUNCTION public.add_triggers_to_new_table(schema_qualified_tablename text)
RETURNS void AS $$
DECLARE
  schema_name text;
  table_name text;

  q_txt text;
BEGIN
  SELECT
    (parse_ident(schema_qualified_tablename))[1],
    (parse_ident(schema_qualified_tablename))[2]
  INTO schema_name, table_name;

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

END;

$$ LANGUAGE plpgsql;
