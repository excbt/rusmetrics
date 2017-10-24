-- Function: portal.register_cont_event(bigint, boolean)

-- DROP FUNCTION portal.register_cont_event(bigint, boolean);

CREATE OR REPLACE FUNCTION portal.register_cont_event(
    p_cont_event_id bigint,
    p_ignore_errors boolean)
  RETURNS void AS
$BODY$
DECLARE
	r_cont_event_type record;
	r_cont_event record;
	v_level_color text;
	v_monitor_result integer = null;
	v_monitor_result_v2 integer = null;
begin

RAISE NOTICE 'Registering cont_event id=% ', p_cont_event_id;

-- Registering monitor
select portal.reg_cont_event_monitor_new (p_cont_event_id, p_ignore_errors) into v_monitor_result;

select portal.reg_cont_event_monitor_new_v2 (p_cont_event_id, p_ignore_errors) into v_monitor_result_v2;

-- v_monitor_result codes	
-- null - if no process.
-- 0 - if no monitor data changed.
-- 1 - if new monitor data added
-- 2 - if "good" event appears
-- 3 - if "worse" event appears, but no monitor data added
-- 4 - if scalar event appears
-- 5 - if same event appears
-- 6 - if no level event appears

if v_monitor_result IN (1,2,3,4) then
	-- Registering notifications
	--PERFORM portal.reg_cont_event_notifications (p_cont_event_id, p_ignore_errors);
        INSERT INTO portal.cont_event_mon_v1_bak(
            cont_event_id)
        VALUES (p_cont_event_id);
end if;	

if v_monitor_result_v2 IN (1,2,3,4,6) then
	-- Registering notifications
	PERFORM portal.reg_cont_event_notifications_v2 (p_cont_event_id, p_ignore_errors);
end if;	

	
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.register_cont_event(bigint, boolean)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_cont_event(bigint, boolean) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_cont_event(bigint, boolean) TO public;
GRANT EXECUTE ON FUNCTION portal.register_cont_event(bigint, boolean) TO portal_role;
