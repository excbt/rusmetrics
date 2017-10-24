-- Function: portal.register_cont_event_v3(bigint, boolean)

-- DROP FUNCTION portal.register_cont_event_v3(bigint, boolean);

CREATE OR REPLACE FUNCTION portal.register_cont_event_v3(
    p_cont_event_id BIGINT,
    p_ignore_errors BOOLEAN)
  RETURNS void AS
$BODY$
DECLARE
	r_cont_event_type RECORD;
	r_cont_event RECORD;
	v_level_color TEXT;
	v_monitor_result INTEGER = NULL;
BEGIN

RAISE NOTICE 'Registering cont_event id=% ', p_cont_event_id;

-- Registering monitor

SELECT portal.reg_cont_event_monitor_v3 (p_cont_event_id, p_ignore_errors) INTO v_monitor_result;

-- v_monitor_result codes	
-- null - if no process.
-- 0 - if no monitor data changed.
-- 1 - if new monitor data added
-- 2 - if "good" event appears
-- 3 - if "worse" event appears, but no monitor data added
-- 4 - if scalar event appears
-- 5 - if same event appears
-- 6 - if no level event appears

if v_monitor_result IN (1,2,3,4,6) then
	-- Registering notifications
	PERFORM portal.reg_cont_event_notifications_v3 (p_cont_event_id, p_ignore_errors);
END IF;	
	
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.register_cont_event_v3(bigint, boolean)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_cont_event_v3(bigint, boolean) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.register_cont_event_v3(bigint, boolean) TO public;
GRANT EXECUTE ON FUNCTION portal.register_cont_event_v3(bigint, boolean) TO portal_role;
