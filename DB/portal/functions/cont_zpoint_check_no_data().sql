-- Function: portal.cont_zpoint_check_no_data()

-- DROP FUNCTION portal.cont_zpoint_check_no_data();

CREATE OR REPLACE FUNCTION portal.cont_zpoint_check_no_data()
  RETURNS BIGINT AS
$BODY$
DECLARE

v_adv_lock                      BOOLEAN;
r_last_data 			RECORD;
r_mon_cont_events               RECORD;
r_cont_event_type_NO_DEV_DATA   RECORD;
v_cnt 				BIGINT = 0;

v_INTERVAL                      INTERVAL := interval '30 days';
v_DEV_NO_DATA                   TEXT := 'DEV_NO_DATA';
v_GREEN_LEVEL                   TEXT := 'GREEN';
v_no_dev_data_cnt               INTEGER;
r_cont_zpoint                   RECORD;
v_cont_event_message            TEXT;
v_check_disabled                BOOLEAN;

v_job_id                        BIGINT;
v_step_id                       BIGINT;
v_jobmon_schema                 TEXT;
v_new_search_path               TEXT := 'public,portal,pg_temp';
v_old_search_path               TEXT;
v_step_overflow_id              BIGINT;	


	--v_process_limit integer = 10;
BEGIN
v_adv_lock := pg_try_advisory_xact_lock(hashtext('portal.cont_zpoint_check_no_data'));
IF v_adv_lock = 'false' THEN
	RAISE NOTICE 'Portal cont_zpoint_check_no_data already running.';
	RETURN 0;
END IF;


SELECT param_value::BOOLEAN INTO v_check_disabled
  FROM system_param
where keyname = 'SP_CONT_EVENT_LAST_DATA_CHECK_DISABLED' and param_group = 'SYSTEM';

IF (v_check_disabled = TRUE) THEN
   RAISE NOTICE 'Portal cont_zpoint_check_no_data is disabled';
   RETURN 0;
END IF;

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

SELECT * INTO r_cont_event_type_NO_DEV_DATA
FROM cont_event_type
WHERE keyname = v_DEV_NO_DATA;

FOR r_last_data IN (	
                        SELECT a.cont_zpoint_id, a.last_data_date, a.last_data_date_time
                        FROM portal.mv_last_data_date_aggr a INNER JOIN cont_zpoint zp ON a.cont_zpoint_id = zp.id
                        WHERE last_data_date < (current_date - v_INTERVAL)::DATE
                        and zp.deleted = 0 
                        and (zp.cont_service_type != 'heat' or
					not exists (SELECT 1 FROM cont_zpoint_setting_mode sm
						    WHERE sm.cont_zpoint_id = zp.id
						    and sm.setting_mode = 'summer'	
						    )
						)	

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
LOOP

        FOR r_mon_cont_events IN (
                                SELECT m.*, ce.cont_zpoint_id, ce.cont_event_message
                                  FROM portal.cont_event_monitor_v3 m LEFT JOIN cont_event ce ON (m.cont_event_id = ce.id)
                                WHERE m.cont_event_type_id in (
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
        LOOP
                
                RAISE NOTICE '===== Found event: cont_event_monitor_id: %, cont_event_id: %, message: %, cont_event_time: %', r_mon_cont_events.id, r_mon_cont_events.cont_event_id, r_mon_cont_events.cont_event_message, r_mon_cont_events.cont_event_time;
                DELETE FROM portal.cont_event_monitor_v3
                WHERE id = r_mon_cont_events.id;

        END LOOP; 

        SELECT zp.* INTO r_cont_zpoint FROM cont_zpoint zp WHERE zp.id = r_last_data.cont_zpoint_id;
        

/*        select count(1) into v_no_dev_data_cnt
        from cont_event m 
        where m.cont_object_id = r_cont_zpoint.cont_object_id
        AND m.cont_zpoint_id = r_cont_zpoint.id
        and m.cont_event_type_id = r_cont_event_type_NO_DEV_DATA.id
        and m.cont_event_time > r_last_data.last_data_date;*/

        SELECT COUNT(1) INTO v_no_dev_data_cnt
        FROM portal.cont_event_monitor_v3 m LEFT JOIN cont_event ce ON (m.cont_event_id = ce.id)
        WHERE m.cont_object_id = r_cont_zpoint.cont_object_id
        AND ce.cont_zpoint_id = r_cont_zpoint.id
        and m.cont_event_type_id = r_cont_event_type_NO_DEV_DATA.id
        and m.cont_event_time > r_last_data.last_data_date;
        

        RAISE NOTICE 'Check NO_DEV_DATA event. cnt:% cont_zpoint_id:%', v_no_dev_data_cnt, r_last_data.cont_zpoint_id;

        IF (r_last_data.last_data_date IS NOT null) then
                v_cont_event_message := format(r_cont_event_type_NO_DEV_DATA.drools_message_template, to_char(r_last_data.last_data_date, 'DD-MM-YYYY'));
        ELSE
                v_cont_event_message := format(r_cont_event_type_NO_DEV_DATA.drools_message_template, 'момента регистрации в системе');
        END IF;
        
        IF (v_no_dev_data_cnt = 0) THEN
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
                                LOCALTIMESTAMP , 
                                v_cont_event_message, 
                                v_cont_event_message, 
                                r_last_data.cont_zpoint_id,
                                LOCALTIMESTAMP );
                v_cnt = v_cnt + 1;                
                
        END IF;

END LOOP;
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
