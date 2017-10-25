-- Function: portal.get_last_monitor_cont_event_id()

-- DROP FUNCTION portal.get_last_monitor_cont_event_id();

CREATE OR REPLACE FUNCTION portal.get_last_monitor_cont_event_id()
  RETURNS bigint AS
$BODY$declare
	v_result bigint = null;
BEGIN
	select MAX(cont_event_id)
	into v_result
	FROM (
		SELECT max(cont_event_id) as cont_event_id , 'MON'
		FROM cont_event_monitor
	union all
		SELECT max(cont_event_id) , 'MON_HIST'
		FROM cont_event_monitor_history
	UNION ALL
		SELECT max(reverse_cont_event_id) , 'MON_HISR'
		FROM cont_event_monitor_history
	UNION all
		SELECT max(cont_event_id) , 'NOTIFY'
		FROM subscr_cont_event_notification
	) as t;

	return v_result;
END;$BODY$
  LANGUAGE plpgsql STABLE
  COST 100;
ALTER FUNCTION portal.get_last_monitor_cont_event_id()
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.get_last_monitor_cont_event_id() TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.get_last_monitor_cont_event_id() TO public;
GRANT EXECUTE ON FUNCTION portal.get_last_monitor_cont_event_id() TO portal_role;
