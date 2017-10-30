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
for subscrs IN (
			SELECT ce.id cont_event_id, ce.cont_object_id, ce.cont_zpoint_id, acc.subscriber_id, s.subscriber_info
			FROM cont_event ce, portal.cont_zpoint_access acc, subscriber s, cont_zpoint zp
			WHERE ce.id = p_cont_event_id AND
						--ce.cont_object_id = acc.cont_object_id and 
						ce.cont_zpoint_id = acc.cont_zpoint_id AND
						acc.subscriber_id = s.id AND
						zp.id = ce.cont_zpoint_id AND
						ce.deleted = 0 AND
						s.deleted = 0 AND
						zp.deleted = 0 AND
						acc.access_ttl_tz IS NULL
			)
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
