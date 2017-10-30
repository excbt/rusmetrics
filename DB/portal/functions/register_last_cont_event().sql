-- Function: portal.register_last_cont_event()

-- DROP FUNCTION portal.register_last_cont_event();

CREATE OR REPLACE FUNCTION portal.register_last_cont_event()
  RETURNS BIGINT AS
$BODY$
declare

v_adv_lock          BOOLEAN;
r_cont_events 		RECORD;
v_cnt 				BIGINT = 0;

v_check_disabled    BOOLEAN;

v_job_id            BIGINT;
v_step_id           BIGINT;
v_jobmon_schema     TEXT;
v_new_search_path   TEXT := 'public,portal,pg_temp';
v_old_search_path   TEXT;
v_step_overflow_id  BIGINT;	
v_process_limit		INTEGER = 500000;

	--v_process_limit integer = 10;
BEGIN
v_adv_lock := pg_try_advisory_xact_lock(hashtext('portal register_last_cont_even'));
IF v_adv_lock = 'false' THEN
	RAISE NOTICE 'Portal register_last_cont_event already running.';
	RETURN 0;
END IF;


SELECT param_value::BOOLEAN INTO v_check_disabled
  FROM system_param
where keyname = 'CONT_EVENT_MONITOR_DISABLED' and param_group = 'SYSTEM';

IF (v_check_disabled = TRUE) THEN
   RAISE NOTICE 'Portal register_last_cont_event is disabled';
   RETURN 0;
END IF;

SELECT current_setting('search_path') INTO v_old_search_path;
SELECT nspname INTO v_jobmon_schema FROM pg_catalog.pg_namespace n, pg_catalog.pg_extension e WHERE e.extname = 'pg_jobmon'::name AND e.extnamespace = n.oid;
IF v_jobmon_schema IS NOT NULL THEN
        v_new_search_path := 'public,portal,'||v_jobmon_schema||',pg_temp';
END IF;

EXECUTE format('SELECT set_config(%L, %L, %L)', 'search_path', v_new_search_path, 'false');

IF v_jobmon_schema IS NOT NULL THEN
    v_job_id := add_job('PORTAL REGISTER LAST CONT EVENT');
    v_step_id := add_step(v_job_id, 'Running register_cont_event_unchecked loop');
END IF;

FOR r_cont_events in (	
			SELECT 	ce.id
			FROM 	cont_event ce, cont_event_type cet
			where 	ce.id > portal.get_last_monitor_cont_event_id() AND
				ce.cont_event_type_id = cet.id AND
				(COALESCE(cet.is_disabled,FALSE) = FALSE) AND
				(COALESCE(cet.is_dev_mode, FALSE) = FALSE)
			ORDER BY ce.cont_event_registration_time_tz, ce.id
			LIMIT v_process_limit
			)
LOOP
	
	PERFORM portal.register_cont_event_unchecked(r_cont_events.id);
	PERFORM portal.register_cont_event_v3(r_cont_events.id, FALSE);
	v_cnt = v_cnt + 1;
	IF (v_cnt > v_process_limit) then
		RETURN v_cnt;
	END IF;
		
END LOOP;
/*	
	return v_cnt;
*/

IF v_jobmon_schema IS NOT NULL THEN
    PERFORM update_step(v_step_id, 'OK', format('Register_cont_event_unchecked loop finished. %s contEvents processed.', v_cnt));
    IF v_step_overflow_id IS NOT NULL THEN
        PERFORM fail_job(v_job_id);
    ELSE
        PERFORM close_job(v_job_id);
    END IF;
END IF;

EXECUTE format('SELECT set_config(%L, %L, %L)', 'search_path', v_old_search_path, 'false');

RETURN v_cnt;	
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.register_last_cont_event()
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO public;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO portal_role;
