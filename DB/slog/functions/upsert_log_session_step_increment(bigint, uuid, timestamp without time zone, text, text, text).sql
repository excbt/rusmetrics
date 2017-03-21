-- Function: slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text)

-- DROP FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text);

CREATE OR REPLACE FUNCTION slog.upsert_log_session_step_increment(
    par_session_id bigint,
    par_step_uuid uuid,
    par_step_date timestamp without time zone,
    par_step_module text,
    par_step_type text,
    par_step_message text)
  RETURNS void AS
$BODY$
declare
    v_step_uuid uuid;    
BEGIN

	if (par_session_id is null or par_step_type is null) then
		RAISE EXCEPTION 'par_session_id OR par_step_type is NULL';
	end if;


    LOOP
        -- first try to update
        --EXECUTE sql_update;

	update slog.log_session_step 
	set 	sum_rows = sum_rows + 1, 
		last_increment_date = GREATEST(last_increment_date, par_step_date)
	where session_id = par_session_id and step_type = par_step_type and is_incremental = true;
        
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
		true, 1, true);   
            
            RETURN;
            EXCEPTION WHEN unique_violation THEN
                -- do nothing and loop
        END;
    END LOOP;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text)
  OWNER TO dbuser1;
