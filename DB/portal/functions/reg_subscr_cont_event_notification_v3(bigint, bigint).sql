-- Function: portal.reg_subscr_cont_event_notification_v3(bigint, bigint)

-- DROP FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint);

CREATE OR REPLACE FUNCTION portal.reg_subscr_cont_event_notification_v3(
    p_subscriber_id bigint,
    p_cont_event_id bigint)
  RETURNS void AS
$BODY$
  declare
	check_id bigint;
	v_cont_event_level integer;
	v_cont_event_category text;
	v_level_color text;

	v_cont_event record;
	v_cont_event_type record;
	v_notification_id bigint;

	r_actions record;
	
BEGIN

	if (p_subscriber_id is null) THEN
		RAISE EXCEPTION 'p_subscriber_id is NULL';
	end if;

/*
	begin
		select s.id into check_id from subscriber s where s.id = p_subscriber_id;
	exception
		WHEN NO_DATA_FOUND THEN
			RAISE EXCEPTION 'subscriber % is not found', p_subscriber_id;
	end;

*/

	select ce.* into v_cont_event from cont_event ce where ce.id = p_cont_event_id;
	if not FOUND then
		RAISE EXCEPTION 'cont_event with id %s is not found', p_cont_event_id;
	end if;

	select cet.cont_event_level_v2, cet.cont_event_category into v_cont_event_type from cont_event_type cet where cet.id = v_cont_event.cont_event_type_id;
	if not FOUND then
		RAISE EXCEPTION 'cont_event_type wit id %s is not found', v_cont_event.cont_event_type_id;
	end if;

	if (v_cont_event_type.cont_event_level_v2 is NOT null) then
		SELECT portal.get_cont_event_level_color_v2(v_cont_event_type.cont_event_level_v2) into v_level_color;
	end if;


	--SELECT cont_event_level, cont_event_category INTO v_cont_event_level, v_cont_event_category 
	--FROM get_cont_event_type_rec(p_cont_event_id);

	INSERT INTO subscr_cont_event_notification(
            subscriber_id, 
            cont_object_id, 
            cont_event_id, 
            cont_event_type_id, 
            cont_event_time, 
            cont_event_level, 
            cont_event_level_color, 
            cont_event_category,
            cont_event_deviation,
            mon_version)
	VALUES (p_subscriber_id, 
		v_cont_event.cont_object_id, 
		p_cont_event_id, 
		v_cont_event.cont_event_type_id,
		v_cont_event.cont_event_time, 
		v_cont_event_type.cont_event_level_v2, 
		v_level_color, 
		v_cont_event_type.cont_event_category,
		v_cont_event.cont_event_deviation,
		2) 
	returning id into v_notification_id;


	RAISE NOTICE 'Processing r_cont_events_actions for cont_event_id=%', p_cont_event_id;
	for r_actions in (
					SELECT subscr_action_user_id, is_sms, is_email
					FROM subscr_cont_event_type_action ceta
					where ceta.subscriber_id = p_subscriber_id and ceta.cont_event_type_id = v_cont_event.cont_event_type_id )
	loop

		if (r_actions.subscr_action_user_id is not null and r_actions.is_sms = true) then
		        -- RAISE NOTICE 'Adding sms notification for %', r_actions.subscr_action_user_id;
			PERFORM portal.reg_subscr_cont_event_action_task(v_notification_id, r_actions.subscr_action_user_id, 'sms');
		end if;

		if (r_actions.subscr_action_user_id is not null and r_actions.is_email = true) then
			-- RAISE NOTICE 'Adding sms notification for %', r_actions.subscr_action_user_id;
			PERFORM portal.reg_subscr_cont_event_action_task(v_notification_id, r_actions.subscr_action_user_id, 'email');
		end if;
	end loop;


END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO public;
GRANT EXECUTE ON FUNCTION portal.reg_subscr_cont_event_notification_v3(bigint, bigint) TO portal_role;
