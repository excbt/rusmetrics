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

perform slog.upsert_log_session_step_increment(par_session_id,par_step_uuid,par_step_date,par_step_module,par_step_type,par_step_message,1);


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION slog.upsert_log_session_step_increment(bigint, uuid, timestamp without time zone, text, text, text)
  OWNER TO dbuser1;
