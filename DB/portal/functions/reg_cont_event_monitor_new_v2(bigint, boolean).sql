-- Function: portal.reg_cont_event_monitor_new_v2(bigint, boolean)

-- DROP FUNCTION portal.reg_cont_event_monitor_new_v2(bigint, boolean);

CREATE OR REPLACE FUNCTION portal.reg_cont_event_monitor_new_v2(
    p_cont_event_id bigint,
    p_ignore_errors boolean)
  RETURNS integer AS
$BODY$
DECLARE
	v_curr_event_type_rec record;
	v_curr_cont_event_rec record;
	v_last_cont_event_rec record;
	v_worse_cont_event_rec record;
	v_level_color text;

	v_worse_deviation_level integer;
	v_curr_deviation_level integer;
	v_worse_cont_event_id integer = null;
	v_worse_cont_event_time timestamp without time zone = null;

	v_monitor_log_id	bigint = null;
	
	-- RETURNS integer codes:
	-- null - if no process.
	-- 0 - if no monitor data changed.
	-- 1 - if new monitor data added
	-- 2 - if "good" event appears
	-- 3 - if "worse" event appears, but no monitor data added
	-- 4 - if scalar event appears
	-- 5 - if same event appears
	-- 6 - if no level event appears

	v_result integer = null;

	V_RES_NO_PROCESS integer = NULL;
	V_RES_NO_CHANGED_DATA integer = 0;
	V_RES_NEW_MONITOR integer = 1;
	V_RES_GOOD_EVENT integer = 2;
	V_RES_WORSE_EVENT integer = 3;
	V_RES_SCALAR_EVENT integer = 4;
	V_RES_SAME_EVENT integer = 5;	
	V_RES_EMPTY_LEVEL integer = 6;

	v_monitor_event_type_id bigint;
	v_monitor_rec record;

	v_check_hist_cont_event_time timestamp without time zone = null;
	v_deleted_count bigint;
BEGIN

	select m.id 
		into v_monitor_log_id
	from portal.v_cont_event_monitor_log_v2 m
	where m.cont_event_id = p_cont_event_id;

	-- we already processed event
	if v_monitor_log_id is not null then
		return V_RES_NO_PROCESS;
	end if;

	-- Reading current cont event 
	select * 
		into v_curr_cont_event_rec
	from cont_event ce
	where ce.id = p_cont_event_id;

	-- Reading current cont event type
	select * 
		into v_curr_event_type_rec
	from cont_event_type cet
	where cet.id = v_curr_cont_event_rec.cont_event_type_id;


        -- If cont event type is disabled or deleted
        if (v_curr_event_type_rec.is_disabled = true or v_curr_event_type_rec.deleted <> 0) then
                return V_RES_NO_PROCESS;
        end if;

	-- scalar 
	if (v_curr_event_type_rec.is_scalar_event = true) then
		return V_RES_SCALAR_EVENT;
	end if;

	-- Check cont event level: no process for empty level
	--if (v_curr_event_type_rec.cont_event_level_v2 is null) then
	--	return V_RES_EMPTY_LEVEL;
	--end if;

	-- Process YELLOW event
	if (v_curr_event_type_rec.is_base_event = false) then
		-- Switch to base event type id
		v_monitor_event_type_id = v_curr_event_type_rec.reverse_id;
	else
		v_monitor_event_type_id = v_curr_event_type_rec.id;
	end if;


	select 	cem.id, cem.cont_event_id, cem.cont_event_level_color, cem.monitor_time, cem.monitor_time_tz, cem.worse_cont_event_id, cem.worse_cont_event_time
	--into 	v_check_id, v_check_timestamp_ntz, v_check_monitor_time, v_check_monitor_time_tz
	into 	v_monitor_rec
	from 	portal.cont_event_monitor_v2 cem
	where 	cem.cont_object_id = v_curr_cont_event_rec.cont_object_id and
		cem.cont_event_type_id = v_monitor_event_type_id
	FOR UPDATE;

	if not FOUND then
		-- EVENT TYPE NOT FOUND
		begin

			-- Check if any newer event for cont_object_id was processed and exists in history
			SELECT 	max(ce.cont_event_time) max_cont_event_time
				into	v_check_hist_cont_event_time
			FROM 	portal.cont_event_monitor_history_v2 mh, cont_event ce
			where 	mh.cont_event_id = ce.id 
			and 	ce.cont_object_id = v_curr_cont_event_rec.cont_object_id
			and 	ce.cont_event_type_id = v_monitor_event_type_id;


			-- If base event && we dont process event or we already processed earlier event before
			if (v_curr_event_type_rec.is_base_event = true) and 
				(v_check_hist_cont_event_time is null or 
				v_curr_cont_event_rec.cont_event_time >= v_check_hist_cont_event_time) then
				
				-- Insertint new base event
				select portal.get_cont_event_level_color_v2(v_curr_event_type_rec.cont_event_level_v2) into v_level_color;

				
				if (v_curr_cont_event_rec.cont_event_deviation is not null) then
					v_worse_cont_event_id = v_curr_cont_event_rec.id;
					v_worse_cont_event_time = v_curr_cont_event_rec.cont_event_time;
				end if;	

				INSERT INTO 	portal.cont_event_monitor_v2(
						cont_object_id, 
						cont_event_id, 
						cont_event_type_id, 
						cont_event_time, 
						cont_event_level, 
						cont_event_level_color,
						last_cont_event_id,
						last_cont_event_time,
						worse_cont_event_id,
						worse_cont_event_time,
						is_scalar
						)
				VALUES (	v_curr_cont_event_rec.cont_object_id, 
						p_cont_event_id, 
						v_curr_cont_event_rec.cont_event_type_id, 
						v_curr_cont_event_rec.cont_event_time, 
						v_curr_event_type_rec.cont_event_level_v2, 
						v_level_color,
						--- last data
						p_cont_event_id,
						v_curr_cont_event_rec.cont_event_time,
						--- worse data
						v_worse_cont_event_id,
						v_worse_cont_event_time,
						-- is scalar sign
						v_curr_event_type_rec.is_scalar_event);


				if (v_curr_event_type_rec.is_scalar_event = true) then
					v_result  = V_RES_SCALAR_EVENT;		
				else			
					v_result  = V_RES_NEW_MONITOR;		
				end if;	
			end if;	
		exception			
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
		end;
	else
		-- FOUND CURRENT EVENT TYPE
		if (v_curr_event_type_rec.is_base_event = true or v_curr_event_type_rec.is_scalar_event = true) then
		
			-- Updating base or scalar event
			update 	portal.cont_event_monitor_v2
			set 	last_cont_event_id = p_cont_event_id, 
				last_cont_event_time = v_curr_cont_event_rec.cont_event_time
			where 	id = v_monitor_rec.id and last_cont_event_time <= v_curr_cont_event_rec.cont_event_time;

			v_result = V_RES_NO_CHANGED_DATA;

			if (v_curr_event_type_rec.is_base_event = true 
				AND v_monitor_rec.worse_cont_event_id is not null 
				and v_curr_cont_event_rec.cont_event_deviation is not null) then
				
				SELECT * 
					INTO v_worse_cont_event_rec
				FROM cont_event ce 
				where ce.id = v_monitor_rec.worse_cont_event_id;

				select d.deviation_level 
					into v_worse_deviation_level 
				from portal.cont_event_deviation d
				WHERE d.keyname = v_worse_cont_event_rec.cont_event_deviation;	

				select d.deviation_level 
					into v_curr_deviation_level 
				from portal.cont_event_deviation d
				WHERE d.keyname = v_curr_cont_event_rec.cont_event_deviation;	


				if (v_curr_deviation_level > v_worse_deviation_level) then
					-- Update worse cont event
					update 	portal.cont_event_monitor_v2 
					set 	worse_cont_event_id = p_cont_event_id, 
						worse_cont_event_time = v_curr_cont_event_rec.cont_event_time
					where 	id = v_monitor_rec.id and worse_cont_event_time <= v_curr_cont_event_rec.cont_event_time;

					if FOUND then
						v_result = V_RES_WORSE_EVENT;
					end if;	
				end if;
				
			end if;
			
		else
			-- Deleting base event
			WITH deleted AS (
				delete 
				FROM 	portal.cont_event_monitor_v2
				--where 	id = v_monitor_rec.id and cont_event_time <= v_curr_cont_event_rec.cont_event_time
				where 	id = v_monitor_rec.id and cont_event_time <= v_curr_cont_event_rec.cont_event_time
				RETURNING *) 
			SELECT count(*) into v_deleted_count FROM deleted;
			
			-- Insert event into history
			if (v_deleted_count > 0) then
				-- Insert History Rec
				INSERT INTO 	portal.cont_event_monitor_history_v2(
						cont_event_id, 
						reverse_cont_event_id, 
						cont_event_level_color, 
						monitor_time, 
						monitor_time_tz)
				VALUES 		(v_monitor_rec.cont_event_id, 
						p_cont_event_id, 
						v_monitor_rec.cont_event_level_color, 
						v_monitor_rec.monitor_time, 
						v_monitor_rec.monitor_time_tz);
			
				v_result = V_RES_GOOD_EVENT;
			end if;
		end if;
	end if;	

return v_result;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.reg_cont_event_monitor_new_v2(bigint, boolean)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_new_v2(bigint, boolean) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_new_v2(bigint, boolean) TO public;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_monitor_new_v2(bigint, boolean) TO portal_role;
