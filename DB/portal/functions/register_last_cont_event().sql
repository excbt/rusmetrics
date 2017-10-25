-- Function: portal.register_last_cont_event()

-- DROP FUNCTION portal.register_last_cont_event();

CREATE OR REPLACE FUNCTION portal.register_last_cont_event()
  RETURNS bigint AS
$BODY$
declare

v_adv_lock                      boolean;
r_cont_events 			record;
v_cnt 				bigint = 0;

v_check_disabled                boolean;

v_job_id                        bigint;
v_step_id                       bigint;
v_jobmon_schema                 text;
v_new_search_path               text := 'public,portal,pg_temp';
v_old_search_path               text;
v_step_overflow_id              bigint;	
v_process_limit integer = 500000;

	--v_process_limit integer = 10;
begin
v_adv_lock := pg_try_advisory_xact_lock(hashtext('portal register_last_cont_even'));
IF v_adv_lock = 'false' THEN
	RAISE NOTICE 'Portal register_last_cont_event already running.';
	RETURN 0;
END IF;


SELECT param_value::boolean into v_check_disabled
  FROM system_param
where keyname = 'CONT_EVENT_MONITOR_DISABLED' and param_group = 'SYSTEM';

if (v_check_disabled = true) then
   RAISE NOTICE 'Portal register_last_cont_event is disabled';
   RETURN 0;
end if;

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

for r_cont_events in (	SELECT 	ce.id
			FROM 	cont_event ce, cont_event_type cet
			where 	ce.id > portal.get_last_monitor_cont_event_id()
				and ce.cont_event_type_id = cet.id 
				and (coalesce(cet.is_disabled,false) = false)
				and (coalesce(cet.is_dev_mode, false) = false)
			order by ce.cont_event_registration_time_tz, ce.id
			limit v_process_limit
			)
loop
	
	PERFORM portal.register_cont_event_unchecked(r_cont_events.id);
	v_cnt = v_cnt + 1;
	if (v_cnt > v_process_limit) then
		return v_cnt;
	end if;
		
end loop;
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

return v_cnt;	
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.register_last_cont_event()
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO public;
GRANT EXECUTE ON FUNCTION portal.register_last_cont_event() TO portal_role;
