-- Function: portal.unregister_cont_event_v3(bigint)

-- DROP FUNCTION portal.unregister_cont_event_v3(bigint);

CREATE OR REPLACE FUNCTION portal.unregister_cont_event_v3(
    p_cont_event_id BIGINT)
  RETURNS void AS
$BODY$
DECLARE
	c_MONITOR_VERSION INTEGER = 3;
BEGIN

RAISE NOTICE 'Registering cont_event id=% ', p_cont_event_id;

  DELETE FROM public.subscr_cont_event_notification
  WHERE cont_event_id = p_cont_event_id and monitor_version = c_MONITOR_VERSION;

  DELETE FROM portal.cont_event_monitor_v3
  WHERE cont_event_id = p_cont_event_id and monitor_version = c_MONITOR_VERSION;

  DELETE FROM portal.cont_event_monitor_history_v3
  WHERE cont_event_id = p_cont_event_id and monitor_version = c_MONITOR_VERSION;

	
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.unregister_cont_event_v3(bigint)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.unregister_cont_event_v3(bigint) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.unregister_cont_event_v3(bigint) TO public;
GRANT EXECUTE ON FUNCTION portal.unregister_cont_event_v3(bigint) TO portal_role;
