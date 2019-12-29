-- ******* CREATE SCHEMAS *******

CREATE SCHEMA auditing;
CREATE SCHEMA auditing_partitions;

CREATE SCHEMA maintenance;

CREATE SCHEMA partitions;





-- ******* SETUP MAINTENANCE UTILITIES *******

CREATE OR REPLACE FUNCTION maintenance.add_check_on_columns(
  sq_table_ident text,
  columns text[])
  RETURNS void
LANGUAGE 'plpgsql'

COST 100
VOLATILE
AS $BODY$

DECLARE

  min_val bigint;
  max_val bigint;
  schema_name text;
  table_name text;
  column_name text;

BEGIN

  SELECT
    (parse_ident(sq_table_ident))[1],
    (parse_ident(sq_table_ident))[2]
  INTO schema_name, table_name;

  FOREACH column_name IN ARRAY columns LOOP

    EXECUTE format(
        $SQL$
                SELECT min(%3$I), max(%3$I) FROM %1$I.%2$I;
            $SQL$, schema_name, table_name, column_name
    ) INTO min_val, max_val;

    IF min_val IS NOT NULL AND max_val IS NOT NULL THEN

      EXECUTE format(
          $SQL$
                    ALTER TABLE %1$I.%2$I ADD CONSTRAINT %3$s_check
                    CHECK (%3$I >= %4$s AND %3$I <= %5$s) NOT VALID;
                $SQL$, schema_name, table_name, column_name,
          min_val, max_val);

    END IF;

  END LOOP;

END;

$BODY$;



CREATE OR REPLACE FUNCTION maintenance.validate_all_check_constraints(
)
  RETURNS void
LANGUAGE 'plpgsql'

COST 100
VOLATILE
AS $BODY$

DECLARE

  r record;

BEGIN

  FOR r IN
  SELECT t.schemaname, t.tablename, c.conname
  FROM pg_tables t INNER JOIN pg_constraint c
      ON c.conrelid = (
      quote_ident(schemaname) || '.' || quote_ident(tablename)
    )::regclass
  WHERE c.contype = 'c' AND NOT c.convalidated
        AND tableowner LIKE '%_migration'
  LOOP

    EXECUTE format(
        $SQL$
                ALTER TABLE %I.%I VALIDATE CONSTRAINT %I;
            $SQL$, r.schemaname, r.tablename, r.conname);

  END LOOP;

END;

$BODY$;



-- ******* SETUP DDL AUDITING *******

--  Create table to audit DDL commands
CREATE TABLE auditing.audit_log_ddl (
  event_id bigserial PRIMARY KEY,
  transaction_timestamp timestamp NOT NULL,
  statement_timestamp timestamp NOT NULL,
  command_timestamp timestamp NOT NULL,
  command text,
  affected_object_type text,
  affected_object_schema text,
  affected_object_name text,
  session_username text NOT NULL,
  client_query text NOT NULL
);



--  Create trigger function to audit generic DDL commands
CREATE OR REPLACE FUNCTION auditing.audit_ddl_command_end() RETURNS event_trigger AS $$
DECLARE

  rec record;

BEGIN

  FOR rec IN SELECT * FROM pg_event_trigger_ddl_commands() LOOP

    INSERT INTO auditing.audit_log_ddl VALUES (
      nextval('auditing.audit_log_ddl_event_id_seq'),
      now(),
      statement_timestamp(),
      clock_timestamp(),
      rec.command_tag,
      upper(rec.object_type),
      rec.schema_name,
      replace(rec.object_identity, COALESCE(rec.schema_name || '.', ''), ''),
      session_user::text,
      current_query()
    );

  END LOOP;

END
$$ LANGUAGE plpgsql
SECURITY DEFINER;



--  Create event trigger to audit generic DDL commands
CREATE EVENT TRIGGER audit_ddl_command_end_trigger
ON ddl_command_end
EXECUTE PROCEDURE auditing.audit_ddl_command_end();



--  Create trigger functions to audit DDL DROP commands
CREATE OR REPLACE FUNCTION auditing.audit_ddl_sql_drop() RETURNS event_trigger AS $$
DECLARE

  rec record;

BEGIN

  FOR rec IN SELECT * FROM pg_event_trigger_dropped_objects() LOOP

    INSERT INTO auditing.audit_log_ddl VALUES (
      nextval('auditing.audit_log_ddl_event_id_seq'),
      now(),
      statement_timestamp(),
      clock_timestamp(),
      'DROP ' || upper(rec.object_type),
      upper(rec.object_type),
      rec.schema_name,
      rec.object_name,
      session_user::text,
      current_query()
    );

  END LOOP;

END
$$ LANGUAGE plpgsql
SECURITY DEFINER;



--  Create event trigger to audit DDL DROP commands
CREATE EVENT TRIGGER audit_ddl_sql_drop_trigger
ON sql_drop
EXECUTE PROCEDURE auditing.audit_ddl_sql_drop();



-- ******* SETUP LIQUIBASE TABLES *******

drop table if exists databasechangeloglock;

CREATE TABLE databasechangeloglock (
  ID INTEGER NOT NULL,
  LOCKED BOOLEAN NOT NULL,
  LOCKGRANTED TIMESTAMP WITHOUT TIME ZONE,
  LOCKEDBY VARCHAR(255),
  CONSTRAINT DATABASECHANGELOGLOCK_PKEY PRIMARY KEY (ID)
);

drop table if exists databasechangelog;

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



-- ******* SETUP DML AUDITING *******

--  Create table to audit DML commands
CREATE TABLE auditing.audit_log_dml (
  event_id bigserial PRIMARY KEY,
  rev_timestamp timestamp NOT NULL,
  tbl_schema text NOT NULL,
  tbl_name text NOT NULL,
  operation text NOT NULL,
  session_username text,
  client_query text,
  row_data jsonb
);



--  Trigger function to audit the DML statements (INSERT/UPDATE/DELETE)
CREATE FUNCTION auditing.audit_dml_row() RETURNS trigger AS $$
DECLARE

  insert_stmt text;
  audit_table_name text;
  audit_row auditing.audit_log_dml;

BEGIN

  IF (TG_WHEN <> 'AFTER' OR TG_LEVEL <> 'ROW') THEN
    RAISE EXCEPTION 'The auditing.audit_dml_row() trigger may only run as an AFTER ROW trigger.';
  END IF;

  audit_row = ROW(
              nextval('auditing.audit_log_dml_event_id_seq'), -- event_id
              clock_timestamp(),                              -- rev_timestamp
              TG_TABLE_SCHEMA::text,                          -- schema_name
              TG_TABLE_NAME::text,                            -- table_name
              left(TG_OP, 1),                                 -- action
              NULL,                                           -- session_user_name
              NULL,                                           -- top-level query or queries (if multistatement) from client
              NULL                                            -- row_data
  );

  IF (TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND (NEW.*) IS DISTINCT FROM (OLD.*))) THEN
    audit_row.row_data = to_jsonb(NEW.*);
  ELSIF (TG_OP = 'DELETE') THEN
    audit_row.row_data = to_jsonb(OLD.*);
  ELSE
    RETURN NULL;
  END IF;

  IF RIGHT(session_user::text, 3) IS DISTINCT FROM '_sw' THEN
    audit_row.session_username = session_user::text;
    audit_row.client_query = current_query();
  END IF;

  audit_table_name := 'audit_log_dml_' || replace(
      audit_row.rev_timestamp::date::text, '-', '_');

  insert_stmt := format(
      'INSERT INTO auditing_partitions.%s
       VALUES ($1.*)', audit_table_name);

  BEGIN

    -- Assume partitions exists
    EXECUTE insert_stmt USING audit_row;

    EXCEPTION WHEN UNDEFINED_TABLE THEN

    BEGIN

      -- Create missing partition
      EXECUTE format(
          $SQL$
                    CREATE TABLE IF NOT EXISTS auditing_partitions.%1$s (
                        PRIMARY KEY(event_id),
                        CHECK (rev_timestamp >= %3$L AND rev_timestamp < %4$L)
                    ) INHERITS (auditing.audit_log_dml);

                    CREATE INDEX IF NOT EXISTS %2$s
                    ON auditing_partitions.%1$s (
                        tbl_schema, tbl_name, rev_timestamp);
                $SQL$,
          audit_table_name,
          audit_table_name || '_idx',
          audit_row.rev_timestamp::date::timestamp::text,
          (audit_row.rev_timestamp + INTERVAL '1 day')::date::timestamp::text
      );

      -- A concurrent txn has created the new partition
      EXCEPTION WHEN OTHERS THEN END;

    -- Now insert into the new partition
    EXECUTE insert_stmt USING audit_row;

  END;


  RETURN NULL;

END;

$$ LANGUAGE plpgsql
VOLATILE NOT LEAKPROOF SECURITY DEFINER;



--  Trigger function to audit the DML TRUNCATE statements
CREATE FUNCTION auditing.audit_truncate_statement() RETURNS trigger AS $$
DECLARE

  insert_stmt text;
  audit_table_name text;
  audit_row auditing.audit_log_dml;

BEGIN

  IF (TG_WHEN <> 'AFTER' OR TG_OP <> 'TRUNCATE' OR TG_LEVEL <> 'STATEMENT') THEN
    RAISE EXCEPTION 'The auditing.audit_truncate_statement() trigger may only run as an AFTER TRUNCATE STATEMENT trigger.';
  END IF;

  audit_row = ROW(
              nextval('auditing.audit_log_dml_event_id_seq'), -- event_id
              clock_timestamp(),                              -- rev_timestamp
              TG_TABLE_SCHEMA::text,                          -- schema_name
              TG_TABLE_NAME::text,                            -- table_name
              left(TG_OP, 1),                                 -- action
              NULL,                                           -- session_user_name
              NULL,                                           -- top-level query or queries (if multistatement) from client
              NULL                                            -- row_data
  );

  IF RIGHT(session_user::text, 3) IS DISTINCT FROM '_sw' THEN
    audit_row.session_username = session_user::text;
    audit_row.client_query = current_query();
  END IF;

  audit_table_name := 'audit_log_dml_' || replace(
      audit_row.rev_timestamp::date::text, '-', '_');

  insert_stmt := format(
      'INSERT INTO auditing_partitions.%s
       VALUES ($1.*)', audit_table_name);

  BEGIN

    -- Assume partition exists
    EXECUTE insert_stmt USING audit_row;

    -- Partition does not exist
    EXCEPTION WHEN UNDEFINED_TABLE THEN

    BEGIN

      -- Create missing partition
      EXECUTE format(
          $SQL$
                    CREATE TABLE IF NOT EXISTS auditing_partitions.%1$s (
                        PRIMARY KEY(event_id),
                        CHECK (rev_timestamp >= %3$L AND rev_timestamp < %4$L)
                    ) INHERITS (auditing.audit_log_dml);

                    CREATE INDEX IF NOT EXISTS %2$s
                    ON auditing_partitions.%1$s (
                        tbl_schema, tbl_name, rev_timestamp);
                $SQL$,
          audit_table_name,
          audit_table_name || '_idx',
          audit_row.rev_timestamp::date::timestamp::text,
          (audit_row.rev_timestamp + INTERVAL '1 day')::date::timestamp::text
      );

      -- A concurrent txn has created the new partition
      EXCEPTION WHEN OTHERS THEN END;

    -- Now insert into the new partition
    EXECUTE insert_stmt USING audit_row;

  END;


  RETURN NULL;

END;

$$ LANGUAGE plpgsql
VOLATILE NOT LEAKPROOF SECURITY DEFINER;



--  Create function to add the DML auditing triggers to a given table
CREATE OR REPLACE FUNCTION auditing.audit_table(target_table text) RETURNS void AS $$
DECLARE

  q_txt text;
  formatted_table_name text;

BEGIN

  formatted_table_name := '"' || replace(replace(target_table, '"', ''), '.', '"."') || '"';

  -- Add auditing for INSERT/UPDATE/DELETE
  EXECUTE format(
      $SQL$
            DROP TRIGGER IF EXISTS audit_dml_row_trigger ON %s;
        $SQL$, formatted_table_name);

  q_txt := format(
      $SQL$
            CREATE TRIGGER audit_dml_row_trigger
            AFTER INSERT OR UPDATE OR DELETE ON %s
            FOR EACH ROW
            EXECUTE PROCEDURE auditing.audit_dml_row();
        $SQL$, formatted_table_name);
  RAISE NOTICE '%', q_txt;
  EXECUTE q_txt;


  -- Add auditing for TRUNCATE
  EXECUTE format(
      $SQL$
            DROP TRIGGER IF EXISTS audit_truncate_statement_trigger ON %s;
        $SQL$, formatted_table_name);

  q_txt := format(
      $SQL$
            CREATE TRIGGER audit_truncate_statement_trigger
            AFTER TRUNCATE ON %s
            FOR EACH STATEMENT
            EXECUTE PROCEDURE auditing.audit_truncate_statement();
        $SQL$, formatted_table_name);
  RAISE NOTICE '%', q_txt;
  EXECUTE q_txt;

END;
$$ LANGUAGE plpgsql;


/*
    Trigger function to set the `created` and `updated` timestamps on INSERT.
    The `created` timestamp is set if NULL or overwritten if the application
    is attempting to provide a custom value for it.
    When used in triggers set on partitions of a table the is partitioned by
    `created`, this function relies on the following arguments:
        1. `lowerbound` - The partition lowerbound [included] (e.g. '2018-06-01')
        2. `upperbound` - The partition upperbound [excluded] (e.g. '2018-06-02')
        3. `part_id_format` - The format of the partition id (either of 'YYYY_MM' and 'YYYY_MM_DD')
    If the timestamp overwriting happens at the gate of a partition, the function
    takes care to ensure that the overwritten value is still compatible with the
    partition boundaries. Should the overwritten value result to be out of bounds,
    the correct partition name is computed based on `part_id_format` and the
    record is re-routed to the new partition.
*/
CREATE OR REPLACE FUNCTION auditing.set_row_time_on_insert() RETURNS trigger AS $$
DECLARE

  part_id text;

  part_id_format text;
  lowerbound date;
  upperbound date;

BEGIN

  BEGIN

    NEW.updated := clock_timestamp();

    EXCEPTION WHEN UNDEFINED_COLUMN THEN END;


  -- The service cannot set the created timestamp
  IF NEW.created IS NULL OR right(session_user::text, 3) = '_sw' THEN

    -- Set or overwrite creation timestamp
    NEW.created := clock_timestamp();

    -- Check if the overwrite leads to a partition mismatch
    IF TG_TABLE_SCHEMA = 'partitions' AND TG_NARGS = 3 THEN

      lowerbound := TG_ARGV[0];
      upperbound := TG_ARGV[1];

      -- Re-route record if this is now the wrong partition
      IF NEW.created::date >= upperbound OR NEW.created::date < lowerbound THEN

        part_id_format := TG_ARGV[2];
        part_id := to_char(NEW.created, part_id_format);

        EXECUTE format(
            'INSERT INTO partitions.%I VALUES ($1.*)',
            overlay(
                TG_TABLE_NAME
                PLACING part_id
                FROM (length(TG_TABLE_NAME) - length(part_id_format) + 1)
            )
        ) USING NEW;

        RETURN NULL;

      END IF;

    END IF;

  END IF;


  RETURN NEW;

END;

$$ LANGUAGE plpgsql;



/*
    Trigger function to set the `created` and `updated` timestamps on UPDATE.
    The function raises an exception if an attempt is made to either
        - update the `created` column
        - update an append-only table (i.e. table lacking an `updated` column)
*/
CREATE OR REPLACE FUNCTION auditing.set_row_time_on_update() RETURNS trigger AS $$
BEGIN

  IF TG_TABLE_NAME != 'streamable_message_types' THEN

    -- Created cannot be updated
    IF NEW.created IS DISTINCT FROM OLD.created THEN

      RAISE EXCEPTION 'The creation timestamp cannot be modified! ¯\_(ツ)_/¯';

    END IF;

  END IF;

  BEGIN

    NEW.updated := clock_timestamp();

    -- The table does not have an `updated` column
    EXCEPTION WHEN undefined_column THEN

    RAISE EXCEPTION 'Cannot update append-only table %.%! ¯\_(ツ)_/¯',
    TG_TABLE_SCHEMA, TG_TABLE_NAME;

  END;

  RETURN NEW;

END;
$$ LANGUAGE plpgsql;



/*
    Function to add the timestamp-setting triggers to a table.
    If the table is a partition of a table partitioned by `created`, the trigger
    to set the `created` and `updated` timestamps on insertion is supplied with
    arguments as specified by the requirements of the corresponding trigger
    function. The arguments in question are infered from the partition name
    based on the naming convention {schema_name}__{table_name}__{part_id}.
    In particular, the {part_id} component of the name is used to determine
    whether the partitioning is done by month or day (based on the format of
    `part_id`) and the lower/upper-bounds of the partition.
*/
CREATE OR REPLACE FUNCTION auditing.chronologize_table(
  schema_qualified_tablename text) RETURNS void AS $$

DECLARE

  schema_name text;
  table_name text;

  part_details record;

  part_id text;
  part_id_format text;

  lowerbound date;
  upperbound date;

  q_txt text;

BEGIN

  SELECT
    (parse_ident(schema_qualified_tablename))[1],
    (parse_ident(schema_qualified_tablename))[2]
  INTO schema_name, table_name;


  EXECUTE format(
      $SQL$
            DROP TRIGGER IF EXISTS set_row_time_on_insert_trigger ON %I.%I;
        $SQL$,
      schema_name, table_name);


  EXECUTE format(
      $SQL$
            SELECT
                cl.relispartition AS is_partition,
                CASE pt.partstrat
                    WHEN 'r' THEN 'RANGE'
                    WHEN 'l' THEN 'LIST'
                END AS partitioning_strategy,
                pt.partnatts AS partitioning_key_cardinality,
                (
                    SELECT
                        array_agg(a.attname)
                    FROM
                        pg_attribute a
                    WHERE
                        a.attrelid = pt.partrelid AND
                        a.attnum = ANY(pt.partattrs)
                    GROUP BY
                        a.attrelid
                )[1] AS partitioning_key
            FROM
                pg_class cl
                LEFT JOIN pg_inherits i ON i.inhrelid = cl.oid
                LEFT JOIN pg_partitioned_table pt ON pt.partrelid = i.inhparent
            WHERE
                cl.oid = %L::regclass
        $SQL$, schema_qualified_tablename
  ) INTO part_details;


  IF  TRUE
      AND schema_name = 'partitions'
      AND part_details.is_partition IS TRUE
      AND part_details.partitioning_strategy IS NOT DISTINCT FROM 'RANGE'
      AND part_details.partitioning_key_cardinality = 1
      AND part_details.partitioning_key IS NOT DISTINCT FROM 'created'
  THEN

    part_id := (regexp_split_to_array(table_name, '__'))[3];

    IF part_id ~ '^\d{4}_\d{2}$' THEN

      part_id_format := 'YYYY_MM';
      lowerbound := to_date(part_id, part_id_format);
      upperbound := lowerbound + INTERVAL '1 month';

    ELSIF part_id ~ '^\d{4}_\d{2}_\d{2}$' THEN

      part_id_format := 'YYYY_MM_DD';
      lowerbound := to_date(part_id, part_id_format);
      upperbound := lowerbound + INTERVAL '1 day';

    ELSE

      RAISE EXCEPTION
      'Partition lowerbound cannot be infered from table name: "%" - "%" is unexpected.',
      table_name, part_id;

    END IF;

    q_txt := format(
        $SQL$
            CREATE TRIGGER set_row_time_on_insert_trigger
            BEFORE INSERT ON %I.%I
            FOR EACH ROW
            EXECUTE PROCEDURE auditing.set_row_time_on_insert(%L, %L, %L);
        $SQL$,
        schema_name, table_name,
        lowerbound::text, upperbound::text, part_id_format);

  ELSE

    q_txt := format(
        $SQL$
            CREATE TRIGGER set_row_time_on_insert_trigger
            BEFORE INSERT ON %I.%I
            FOR EACH ROW
            EXECUTE PROCEDURE auditing.set_row_time_on_insert();
        $SQL$,
        schema_name, table_name);

  END IF;

  RAISE NOTICE '%', q_txt;

  EXECUTE q_txt;


  EXECUTE format(
      $SQL$
            DROP TRIGGER IF EXISTS set_row_time_on_update_trigger ON %I.%I;
        $SQL$,
      schema_name, table_name);

  q_txt := format(
      $SQL$
            CREATE TRIGGER set_row_time_on_update_trigger
            BEFORE UPDATE ON %I.%I
            FOR EACH ROW
            EXECUTE PROCEDURE auditing.set_row_time_on_update();
        $SQL$,
      schema_name, table_name);

  RAISE NOTICE '%', q_txt;

  EXECUTE q_txt;

END;

$$ LANGUAGE plpgsql;



--  Trigger function to validate and autoconfigure new service tables
CREATE OR REPLACE FUNCTION auditing.configure_table()
  RETURNS event_trigger
LANGUAGE 'plpgsql'
COST 100
VOLATILE NOT LEAKPROOF
AS $BODY$

DECLARE

  rec RECORD;

  schema_name text;
  table_name text;

  is_partitioned boolean;

BEGIN

  FOR rec IN SELECT * FROM pg_event_trigger_ddl_commands() LOOP

    IF rec.command_tag = 'CREATE TABLE' AND upper(rec.object_type) = 'TABLE' THEN

      SELECT
        (parse_ident(rec.object_identity))[1],
        (parse_ident(rec.object_identity))[2]
      INTO schema_name, table_name;

      -- Only for non-temporary tables
      IF schema_name != 'pg_temp' THEN

        EXECUTE format(
            $SQL$
                            SELECT cl.relkind = 'p' FROM pg_class cl
                            WHERE cl.oid = %L::regclass
                        $SQL$, rec.object_identity
        ) INTO is_partitioned;

        IF  NOT is_partitioned
            AND schema_name NOT IN (
          'maintenance',
          'auditing',
          'auditing_partitions')
            AND table_name NOT IN ('databasechangelog', 'databasechangeloglock')
        THEN

          PERFORM auditing.audit_table(rec.object_identity);
          PERFORM auditing.chronologize_table(rec.object_identity);

        END IF;

      END IF;

    END IF;

  END LOOP;

END

$BODY$;



--  Event trigger to add DML auditing to every new service table by default
CREATE EVENT TRIGGER configure_table_ddl_command_end_trigger
ON DDL_COMMAND_END
EXECUTE PROCEDURE auditing.configure_table();
