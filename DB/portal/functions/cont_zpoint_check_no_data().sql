-- Function: portal.cont_zpoint_check_no_data()

-- DROP FUNCTION portal.cont_zpoint_check_no_data();

CREATE OR REPLACE FUNCTION portal.cont_zpoint_check_no_data()
  RETURNS bigint AS
$BODY$
declare

v_adv_lock                      boolean;
r_last_data 			record;
r_mon_cont_events               record;
r_cont_event_type_NO_DEV_DATA   record;
v_cnt 				bigint = 0;

v_INTERVAL                      INTERVAL := interval '30 days';
v_DEV_NO_DATA                   text := 'DEV_NO_DATA';
v_GREEN_LEVEL                   text := 'GREEN';
v_no_dev_data_cnt               integer;
r_cont_zpoint                   record;
v_cont_event_message            text;
v_check_disabled                boolean;

v_job_id                        bigint;
v_step_id                       bigint;
v_jobmon_schema                 text;
v_new_search_path               text := 'public,portal,pg_temp';
v_old_search_path               text;
v_step_overflow_id              bigint;	


	--v_process_limit integer = 10;
begin
v_adv_lock := pg_try_advisory_xact_lock(hashtext('portal.cont_zpoint_check_no_data'));
IF v_adv_lock = 'false' THEN
	RAISE NOTICE 'Portal cont_zpoint_check_no_data already running.';
	RETURN 0;
END IF;


SELECT param_value::boolean into v_check_disabled
  FROM system_param
where keyname = 'SP_CONT_EVENT_LAST_DATA_CHECK_DISABLED' and param_group = 'SYSTEM';

if (v_check_disabled = true) then
   RAISE NOTICE 'Portal cont_zpoint_check_no_data is disabled';
   RETURN 0;
end if;

SELECT current_setting('search_path') INTO v_old_search_path;
SELECT nspname INTO v_jobmon_schema FROM pg_catalog.pg_namespace n, pg_catalog.pg_extension e WHERE e.extname = 'pg_jobmon'::name AND e.extnamespace = n.oid;
IF v_jobmon_schema IS NOT NULL THEN
        v_new_search_path := 'public,portal,'||v_jobmon_schema||',pg_temp';
END IF;

EXECUTE format('SELECT set_config(%L, %L, %L)', 'search_path', v_new_search_path, 'false');

IF v_jobmon_schema IS NOT NULL THEN
    v_job_id := add_job('PORTAL CONT_ZPOINT CHECK NO DATA');
    v_step_id := add_step(v_job_id, 'Running cont_zpoint_check_no_data loop');
END IF;

select * into r_cont_event_type_NO_DEV_DATA
from cont_event_type
where keyname = v_DEV_NO_DATA;

for r_last_data in (	
                        SELECT cont_zpoint_id, last_data_date, last_data_date_time
                        FROM mv_last_data_date_aggr
                        where last_data_date < (current_date - v_INTERVAL)::date

                        UNION ALL

                        SELECT zp.id,
                                NULL last_data_date,
                                NULL last_data_date_time
                        FROM cont_zpoint zp
                        INNER JOIN cont_object co ON zp.cont_object_id = co.id
                        WHERE zp.id NOT IN (
                                        SELECT cont_zpoint_id
                                        FROM portal.mv_last_data_date_aggr
                                        )
                                AND zp.deleted = 0
                                AND co.deleted = 0                        
			)
loop

        for r_mon_cont_events in (
                                SELECT m.*, ce.cont_zpoint_id, ce.cont_event_message
                                  FROM portal.cont_event_monitor_v2 m LEFT JOIN cont_event ce ON (m.cont_event_id = ce.id)
                                where m.cont_event_type_id in (
                                                        SELECT id AS cont_event_type_id
                                                        FROM cont_event_type
                                                        WHERE (is_scalar_event IS NULL
                                                                OR is_scalar_event = false
                                                                AND is_base_event = true
                                                                AND cont_event_level_v2 < (
                                                                        SELECT least(clr.cont_event_level_from, clr.cont_event_level_to) lvl
                                                                        FROM portal.cont_event_level_color_v2 clr
                                                                        WHERE clr.keyname = v_GREEN_LEVEL
                                                                        )  
                                                                )
                                                               and keyname <> v_DEV_NO_DATA       
                                                        )
                                and ce.cont_zpoint_id = r_last_data.cont_zpoint_id                       
                        ) 
        loop
                
                RAISE NOTICE '===== Found event: cont_event_monitor_id: %, cont_event_id: %, message: %, cont_event_time: %', r_mon_cont_events.id, r_mon_cont_events.cont_event_id, r_mon_cont_events.cont_event_message, r_mon_cont_events.cont_event_time;
                DELETE FROM portal.cont_event_monitor_v2
                WHERE id = r_mon_cont_events.id;

        end loop;                

        select zp.* into r_cont_zpoint from cont_zpoint zp where zp.id = r_last_data.cont_zpoint_id;
        

        select count(1) into v_no_dev_data_cnt
        from cont_event m 
        where m.cont_object_id = r_cont_zpoint.cont_object_id
        and m.cont_event_type_id = r_cont_event_type_NO_DEV_DATA.id
        and m.cont_event_time > r_last_data.last_data_date;

        RAISE NOTICE 'Check NO_DEV_DATA event. cnt:% cont_zpoint_id:%', v_no_dev_data_cnt, r_last_data.cont_zpoint_id;

        if (r_last_data.last_data_date <> null) then
                v_cont_event_message := format(r_cont_event_type_NO_DEV_DATA.drools_message_template, to_char(r_last_data.last_data_date, 'DD-MM-YYYY'));
        else
                v_cont_event_message := format(r_cont_event_type_NO_DEV_DATA.drools_message_template, 'момента регистрации в системе');
        end if;        
        
        if (v_no_dev_data_cnt = 0) then
                RAISE NOTICE 'Creating NO_DEV_DATA event for cont_zpoint_id: %, v_cont_event_message: %', r_last_data.cont_zpoint_id, v_cont_event_message;

                INSERT INTO cont_event(
                                cont_object_id, 
                                cont_event_type_id, 
                                cont_service_type, 
                                cont_event_time, 
                                cont_event_message, 
                                cont_event_comment, 
                                cont_zpoint_id, 
                                cont_event_registration_time_tz)
                    VALUES (    r_cont_zpoint.cont_object_id,
                                r_cont_event_type_NO_DEV_DATA.id, 
                                r_cont_zpoint.cont_service_type, 
                                localTIMESTAMP , 
                                v_cont_event_message, 
                                v_cont_event_message, 
                                r_last_data.cont_zpoint_id,
                                localTIMESTAMP );
                v_cnt = v_cnt + 1;                
                
        end if;

        
        
end loop;
/*	
	return v_cnt;
*/

IF v_jobmon_schema IS NOT NULL THEN
    PERFORM update_step(v_step_id, 'OK', format('cont_zpoint_check_no_data loop finished. %s contZpoints processed.', v_cnt));
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
ALTER FUNCTION portal.cont_zpoint_check_no_data()
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO public;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO portal_role;
