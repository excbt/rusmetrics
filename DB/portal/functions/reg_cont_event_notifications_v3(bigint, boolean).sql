-- Function: portal.reg_cont_event_notifications_v3(bigint, boolean)

-- DROP FUNCTION portal.reg_cont_event_notifications_v3(bigint, boolean);

CREATE OR REPLACE FUNCTION portal.reg_cont_event_notifications_v3(
    p_cont_event_id bigint,
    p_ignore_errors boolean)
  RETURNS void AS
$BODY$
DECLARE
    subscrs RECORD;
begin

RAISE NOTICE 'Registering notification for cont_event id=% ', p_cont_event_id;

-- Registering notifications
for subscrs in select ce.id cont_event_id, sco.cont_object_id, sco.subscriber_id, s.subscriber_info
		from cont_event ce, subscr_cont_object sco, subscriber s, cont_object co
		where 	ce.id = p_cont_event_id and 
			ce.cont_object_id = sco.cont_object_id and 
			sco.subscriber_id = s.id and
			sco.cont_object_id = co.id and 
			ce.deleted = 0 and
			s.deleted = 0 and 
			co.deleted = 0 AND
			sco.subscr_end_date is null
loop
	--RAISE NOTICE 'Processing subscriber id=% info=%', subscrs.subscriber_id, subscrs.subscriber_info;
	BEGIN
		PERFORM portal.reg_subscr_cont_event_notification_v3(subscrs.subscriber_id,subscrs.cont_event_id);
	EXCEPTION
		when OTHERS THEN 
			IF p_ignore_errors = TRUE THEN
				RAISE NOTICE 'Ingore Error during register cont_event for subscriber_id=%, cont_event_id=%, SQLERRM=%, SQLSTATE=%', subscrs.subscriber_id,subscrs.cont_event_id, SQLERRM, SQLSTATE;
			ELSE
				RAISE;
				--RAISE EXCEPTION  '% %', SQLERRM, SQLSTATE;
				--RAISE EXCEPTION 'Error during register cont_event for subscriber_id=%, cont_event_id=%', subscrs.subscriber_id,subscrs.cont_event_id;
			END IF;
			
	END;	

end loop;

	
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.reg_cont_event_notifications_v3(bigint, boolean)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_notifications_v3(bigint, boolean) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_notifications_v3(bigint, boolean) TO public;
GRANT EXECUTE ON FUNCTION portal.reg_cont_event_notifications_v3(bigint, boolean) TO portal_role;
