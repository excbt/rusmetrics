-- Function: portal.reg_subscr_cont_event_notification_v3(bigint, bigint)

-- DROP FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint);

CREATE OR REPLACE FUNCTION portal.reg_subscr_cont_event_notification_v3(
    p_subscriber_id BIGINT,
    p_cont_event_id BIGINT)
  RETURNS void AS
$BODY$
  DECLARE
	check_id BIGINT;
	v_cont_event_level INTEGER;
	v_cont_event_category TEXT;
	v_level_color TEXT;

	v_cont_event RECORD;
	v_cont_event_type RECORD;
	v_notification_id BIGINT;

	r_actions RECORD;
	
	c_MONITOR_VERSION INTEGER = 3;

BEGIN

	IF (p_subscriber_id is null) THEN
		RAISE EXCEPTION 'p_subscriber_id is NULL';
	END IF;

/*
	begin
		select s.id into check_id from subscriber s where s.id = p_subscriber_id;
	exception
		WHEN NO_DATA_FOUND THEN
			RAISE EXCEPTION 'subscriber % is not found', p_subscriber_id;
	end;

*/

	SELECT ce.* 
		INTO v_cont_event 
	FROM cont_event ce 
	WHERE ce.id = p_cont_event_id;
	IF NOT FOUND THEN
		RAISE EXCEPTION 'cont_event with id %s is not found', p_cont_event_id;
	END IF;

	SELECT cet.cont_event_level_v2, cet.cont_event_category 
		INTO v_cont_event_type 
	FROM cont_event_type cet 
	WHERE cet.id = v_cont_event.cont_event_type_id;
	IF NOT FOUND THEN
		RAISE EXCEPTION 'cont_event_type wit id %s is not found', v_cont_event.cont_event_type_id;
	END IF;

	IF (v_cont_event_type.cont_event_level_v2 IS NOT NULL) THEN
		SELECT portal.get_cont_event_level_color_v2(v_cont_event_type.cont_event_level_v2) INTO v_level_color;
	END IF;


	--SELECT cont_event_level, cont_event_category INTO v_cont_event_level, v_cont_event_category 
	--FROM get_cont_event_type_rec(p_cont_event_id);

	INSERT INTO subscr_cont_event_notification(
            subscriber_id, --1
            cont_object_id, --2
			cont_zpoint_id, --3
            cont_event_id, --4
            cont_event_type_id, --5
            cont_event_time, --6
            cont_event_level, --7
            cont_event_level_color, --8
            cont_event_category, --9
            cont_event_deviation, --10
            mon_version --11
			)
	VALUES (
		p_subscriber_id, --1
		v_cont_event.cont_object_id, --2
		v_cont_event.cont_zpoint_id, --3
		p_cont_event_id, --4
		v_cont_event.cont_event_type_id, --5
		v_cont_event.cont_event_time, --6
		v_cont_event_type.cont_event_level_v2, --7
		v_level_color, --8
		v_cont_event_type.cont_event_category, --9
		v_cont_event.cont_event_deviation, --10
		c_MONITOR_VERSION --11
		) 
	RETURNING id INTO v_notification_id;


	RAISE NOTICE 'Processing r_cont_events_actions for cont_event_id=%', p_cont_event_id;
	FOR R_ACTIONS IN (
					SELECT subscr_action_user_id, is_sms, is_email
					FROM subscr_cont_event_type_action ceta
					WHERE ceta.subscriber_id = p_subscriber_id AND 
						ceta.cont_event_type_id = v_cont_event.cont_event_type_id 
					)
	LOOP

		IF (r_actions.subscr_action_user_id IS NOT NULL AND r_actions.is_sms = TRUE) THEN
		        -- RAISE NOTICE 'Adding sms notification for %', r_actions.subscr_action_user_id;
			PERFORM portal.reg_subscr_cont_event_action_task(v_notification_id, r_actions.subscr_action_user_id, 'sms');
		END IF;

		IF (r_actions.subscr_action_user_id IS NOT NULL AND r_actions.is_email = TRUE) THEN
			-- RAISE NOTICE 'Adding sms notification for %', r_actions.subscr_action_user_id;
			PERFORM portal.reg_subscr_cont_event_action_task(v_notification_id, r_actions.subscr_action_user_id, 'email');
		END IF;
	END LOOP;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO public;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO portal_role;
