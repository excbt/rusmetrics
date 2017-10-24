-- Function: portal.reg_cont_event_monitor_v3(bigint, boolean)

-- DROP FUNCTION portal.reg_cont_event_monitor_v3(bigint, boolean);

CREATE OR REPLACE FUNCTION portal.reg_cont_event_monitor_v3(
    p_cont_event_id BIGINT,
    p_ignore_errors BOOLEAN)
  RETURNS integer AS
$BODY$
DECLARE
	v_curr_event_type_rec RECORD;
	v_curr_cont_event_rec RECORD;
	v_last_cont_event_rec RECORD;
	v_worse_cont_event_rec RECORD;
	v_level_color TEXT;

	v_worse_deviation_level INTEGER;
	v_curr_deviation_level INTEGER;
	v_worse_cont_event_id INTEGER = NULL;
	v_worse_cont_event_time timestamp without time zone = NULL;

	v_monitor_log_id	BIGINT = NULL;
	
	c_MONITOR_VERSION INTEGER = 3;

	-- RETURNS integer codes:
	-- null - if no process.
	-- 0 - if no monitor data changed.
	-- 1 - if new monitor data added
	-- 2 - if "good" event appears
	-- 3 - if "worse" event appears, but no monitor data added
	-- 4 - if scalar event appears
	-- 5 - if same event appears
	-- 6 - if no level event appears

	v_result INTEGER = NULL;

	V_RES_NO_PROCESS INTEGER = NULL;
	V_RES_NO_NEW_DATA INTEGER = 0;
	V_RES_NEW_MONITOR INTEGER = 1;
	V_RES_GOOD_EVENT INTEGER = 2;
	V_RES_WORSE_EVENT INTEGER = 3;
	V_RES_SCALAR_EVENT INTEGER = 4;
	V_RES_SAME_EVENT INTEGER = 5;	
	V_RES_EMPTY_LEVEL INTEGER = 6;

	v_monitor_event_type_id BIGINT;
	v_same_monitor_rec RECORD;

	v_check_hist_cont_event_time timestamp without time zone = NULL;
	v_deleted_count BIGINT;
BEGIN

	SELECT m.id 
		INTO v_monitor_log_id
	FROM portal.v_cont_event_monitor_log_v2 m
	WHERE m.cont_event_id = p_cont_event_id;

	-- we already processed event
	IF v_monitor_log_id IS NOT NULL THEN
		RETURN V_RES_NO_PROCESS;
	END IF;

	-- Reading current cont event 
	SELECT * 
		INTO v_curr_cont_event_rec
	FROM cont_event ce
	WHERE ce.id = p_cont_event_id;

	-- Reading current cont event type
	SELECT * 
		INTO v_curr_event_type_rec
	FROM cont_event_type cet
	WHERE cet.id = v_curr_cont_event_rec.cont_event_type_id;


    -- If cont event type is disabled or deleted
    IF (v_curr_event_type_rec.is_disabled = TRUE or v_curr_event_type_rec.deleted <> 0) THEN
            RETURN V_RES_NO_PROCESS;
    END IF;

	-- CHECK: scalar 
	IF (v_curr_event_type_rec.is_scalar_event = TRUE) THEN
		RETURN V_RES_SCALAR_EVENT;
	END IF;

	-- Check cont event level: no process for empty level
	--if (v_curr_event_type_rec.cont_event_level_v2 is null) then
	--	return V_RES_EMPTY_LEVEL;
	--end if;

	-- Process YELLOW event
	IF (v_curr_event_type_rec.is_base_event = FALSE) THEN
		-- Switch to base event type id
		v_monitor_event_type_id = v_curr_event_type_rec.reverse_id;
	ELSE
		v_monitor_event_type_id = v_curr_event_type_rec.id;
	END IF;


	SELECT 	cem.id, cem.cont_event_id, cem.cont_event_level_color, cem.monitor_time, cem.monitor_time_tz, cem.worse_cont_event_id, cem.worse_cont_event_time
	--into 	v_check_id, v_check_timestamp_ntz, v_check_monitor_time, v_check_monitor_time_tz
	INTO 	v_same_monitor_rec
	FROM 	portal.cont_event_monitor_v3 cem
	WHERE 	cem.cont_object_id = v_curr_cont_event_rec.cont_object_id 
		AND cem.cont_zpoint_id = v_curr_cont_event_rec.cont_zpoint_id 
		AND cem.cont_event_type_id = v_monitor_event_type_id
		AND cem.monitor_version = c_MONITOR_VERSION
	FOR UPDATE;

	-- IF NOT FOUND current event type in monitor (v_same_monitor_rec)
	IF NOT FOUND then
		-- event type NOT FOUND
		BEGIN

			-- Check if any newer event for cont_object_id & cont_zpoint_id  was processed and exists in history
			-- cont_event_time - internal time of device/object/etc
			SELECT 	MAX(ce.cont_event_time) max_cont_event_time
				INTO	v_check_hist_cont_event_time
			FROM 	portal.cont_event_monitor_history_v3 mh, cont_event ce
			WHERE 	mh.cont_event_id = ce.id 
				AND ce.cont_object_id = v_curr_cont_event_rec.cont_object_id
				AND ce.cont_zpoint_id = v_curr_cont_event_rec.cont_zpoint_id
				AND ce.cont_event_type_id = v_monitor_event_type_id
				AND mh.monitor_version = c_MONITOR_VERSION;


			-- If base event && we dont process event or we already processed earlier event before
			IF (v_curr_event_type_rec.is_base_event = TRUE) AND 
				(v_check_hist_cont_event_time IS NULL OR v_curr_cont_event_rec.cont_event_time >= v_check_hist_cont_event_time) THEN
				
				-- Insertint new base event
				-- Gets level color
				SELECT portal.get_cont_event_level_color_v2(v_curr_event_type_rec.cont_event_level_v2) INTO v_level_color;

				
				IF (v_curr_cont_event_rec.cont_event_deviation IS NOT NULL) THEN
					v_worse_cont_event_id = v_curr_cont_event_rec.id;
					v_worse_cont_event_time = v_curr_cont_event_rec.cont_event_time;
				END IF;	

				INSERT INTO portal.cont_event_monitor_v3(
								cont_object_id, --1
								cont_zpoint_id, --1.1
								cont_event_id, --2
								cont_event_type_id, --3
								cont_event_time, --4
								cont_event_level, --5
								cont_event_level_color, --6
								last_cont_event_id, --7
								last_cont_event_time, --8
								worse_cont_event_id, --9
								worse_cont_event_time, --10
								is_scalar, 			--11
								monitor_version
								)
				VALUES (v_curr_cont_event_rec.cont_object_id, --1
						v_curr_cont_event_rec.cont_zpoint_id, --1.1
						p_cont_event_id, --2
						v_curr_cont_event_rec.cont_event_type_id, --3
						v_curr_cont_event_rec.cont_event_time, --4
						v_curr_event_type_rec.cont_event_level_v2, --5
						v_level_color, --6
						--- last data
						p_cont_event_id, --7
						v_curr_cont_event_rec.cont_event_time, --8
						--- worse data
						v_worse_cont_event_id, --9
						v_worse_cont_event_time, --10
						-- is scalar sign
						v_curr_event_type_rec.is_scalar_event,
						c_MONITOR_VERSION --11
						);


				IF (v_curr_event_type_rec.is_scalar_event = TRUE) THEN
					v_result  = V_RES_SCALAR_EVENT;		
				ELSE			
					v_result  = V_RES_NEW_MONITOR;		
				END IF;	
			END IF;	
		EXCEPTION			
		when OTHERS THEN 
			IF p_ignore_errors = TRUE THEN
				RAISE NOTICE 'Ingore Error during register_cont_event_monitor_v2 for cont_event_id=%, SQLERRM=%, SQLSTATE=%', p_cont_event_id,SQLERRM, SQLSTATE;
				v_result = V_RES_NO_PROCESS;
			ELSE
				RAISE;
				--RAISE EXCEPTION  '% %', SQLERRM, SQLSTATE;
				--RAISE EXCEPTION 'Error during register cont_event for subscriber_id=%, cont_event_id=%', subscrs.subscriber_id,subscrs.cont_event_id;
			END IF;			
			--v_result = V_RES_NO_PROCESS;
		END;
	ELSE
		-- if FOUND current event type in monitor (v_same_monitor_rec)
		-- if event is scalar is redundant, becouse we check event in CHECK: scalar (line ~ 82)
		IF (v_curr_event_type_rec.is_base_event = TRUE OR v_curr_event_type_rec.is_scalar_event = TRUE) THEN
		
			-- Updating last_* fields of existing rec with values from new event
			UPDATE 	portal.cont_event_monitor_v3
			SET 	last_cont_event_id = p_cont_event_id, 
					last_cont_event_time = v_curr_cont_event_rec.cont_event_time
			WHERE id = v_same_monitor_rec.id 
				AND last_cont_event_time <= v_curr_cont_event_rec.cont_event_time;

			v_result = V_RES_NO_NEW_DATA;

			IF (v_curr_event_type_rec.is_base_event = TRUE 
				AND v_same_monitor_rec.worse_cont_event_id IS NOT NULL 
				AND v_curr_cont_event_rec.cont_event_deviation IS NOT NULL) THEN
				
				SELECT * 
					INTO v_worse_cont_event_rec
				FROM cont_event ce 
				WHERE ce.id = v_same_monitor_rec.worse_cont_event_id;

				SELECT d.deviation_level 
					INTO v_worse_deviation_level 
				FROM portal.cont_event_deviation d
				WHERE d.keyname = v_worse_cont_event_rec.cont_event_deviation;	

				SELECT d.deviation_level 
					INTO v_curr_deviation_level 
				FROM portal.cont_event_deviation d
				WHERE d.keyname = v_curr_cont_event_rec.cont_event_deviation;	


				if (v_curr_deviation_level > v_worse_deviation_level) then
					-- Update worse cont event
					UPDATE 	portal.cont_event_monitor_v3 
					SET	worse_cont_event_id = p_cont_event_id, 
						worse_cont_event_time = v_curr_cont_event_rec.cont_event_time
					WHERE id = v_same_monitor_rec.id and worse_cont_event_time <= v_curr_cont_event_rec.cont_event_time;

					IF FOUND then
						v_result = V_RES_WORSE_EVENT;
					END IF;	
				END IF;
				
			END IF;
			
		ELSE
			-- Deleting base event
			WITH deleted AS (
				DELETE 
				FROM 	portal.cont_event_monitor_v3
				--where 	id = v_same_monitor_rec.id and cont_event_time <= v_curr_cont_event_rec.cont_event_time
				WHERE 	id = v_same_monitor_rec.id and cont_event_time <= v_curr_cont_event_rec.cont_event_time
				RETURNING *) 
			SELECT count(*) INTO v_deleted_count FROM deleted;
			
			-- Insert event into history
			if (v_deleted_count > 0) then
				-- Insert History Rec
				INSERT INTO 	portal.cont_event_monitor_history_v3(
						cont_event_id, 
						reverse_cont_event_id, 
						cont_event_level_color, 
						monitor_time, 
						monitor_time_tz,
						monitor_version
						)
				VALUES 	(
						v_same_monitor_rec.cont_event_id, 
						p_cont_event_id, 
						v_same_monitor_rec.cont_event_level_color, 
						v_same_monitor_rec.monitor_time, 
						v_same_monitor_rec.monitor_time_tz,
						c_MONITOR_VERSION
						);
			
				v_result = V_RES_GOOD_EVENT;
			END IF;
		END IF;
	END IF;	

RETURN v_result;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.reg_cont_event_monitor_v3(bigint, boolean)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_v3(bigint, boolean) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_v3(bigint, boolean) TO public;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_v3(bigint, boolean) TO portal_role;
