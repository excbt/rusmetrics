-- Function: slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text, integer)

-- DROP FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text, integer);

CREATE OR REPLACE FUNCTION slog.upsert_log_session_step_increment(
    par_session_id bigint,
    par_step_uuid uuid,
    par_step_date timestamp without time zone,
    par_step_module text,
    par_step_type text,
    par_step_message text,
    par_inc_count integer)
  RETURNS void AS
$BODY$
declare
    v_step_uuid uuid;    
    v_inc_count integer := coalesce(par_inc_count, 1);
BEGIN

    if (par_session_id is null or par_step_type is null) then
		RAISE EXCEPTION 'par_session_id OR par_step_type is NULL';
    end if;


    LOOP
        -- first try to update
        --EXECUTE sql_update;

        WITH lss AS (
                SELECT session_id,step_type,is_incremental
                FROM   slog.log_session_step 
                WHERE  session_id = par_session_id and step_type = par_step_type and is_incremental = true
                FOR UPDATE SKIP LOCKED
        )
	update slog.log_session_step s
	set 	sum_rows = sum_rows + v_inc_count, 
		last_increment_date = GREATEST(last_increment_date, par_step_date)
	FROM   lss
	where s.session_id = par_session_id and s.step_type = par_step_type and s.is_incremental = true;
        
        -- check if the row is found
        IF FOUND THEN
            RETURN;
        END IF;
        -- not found so insert the row
        BEGIN
            --EXECUTE sql_insert;


        if (par_step_uuid is not null ) then
                v_step_uuid := par_step_uuid;
        else
                v_step_uuid := uuid_generate_v1();
        end if;
        

	INSERT INTO slog.log_session_step(
		session_id, step_uuid, step_date, step_module, step_type, 
		step_message, is_checked, sum_rows, is_incremental)
	VALUES (par_session_id, v_step_uuid, par_step_date, par_step_module, par_step_type, par_step_message, 
		true, v_inc_count, true);   
            
            RETURN;
            EXCEPTION WHEN unique_violation THEN
                -- do nothing and loop
                PERFORM pg_sleep(0.01);
        END;
    END LOOP;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text, integer)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text, integer) TO public;
GRANT EXECUTE ON FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text, integer) TO dbuser1;
