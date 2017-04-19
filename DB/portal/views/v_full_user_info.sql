CREATE OR REPLACE VIEW portal.v_full_user_info AS 
 SELECT system_user.id,
    system_user.user_name,
    system_user.first_name,
    system_user.last_name,
    system_user.version,
    system_user.subscriber_id,
    true AS is_system,
    system_user.user_uuid,
    true AS is_admin,
    false AS is_readonly,
    true AS can_create_child,
    false AS is_child,
    'MASTER_ADMIN'::text AS subscr_type,
    false as is_blocked
   FROM system_user
  WHERE system_user.id > 0 AND system_user.deleted = 0
UNION
 SELECT subscr_user.id,
    subscr_user.user_name,
    subscr_user.first_name,
    subscr_user.last_name,
    subscr_user.version,
    subscr_user.subscriber_id,
    false AS is_system,
    subscr_user.user_uuid,
    subscr_user.is_admin,
    COALESCE(subscr_user.is_readonly, false) AS is_readonly,
    COALESCE(subscriber.can_create_child, false) AS can_create_child,
    COALESCE(subscriber.is_child, false) AS is_child,
    subscriber.subscr_type,
    COALESCE(subscr_user.is_blocked, false) AS is_blocked
   FROM subscr_user,
    subscriber
  WHERE subscr_user.id > 0 AND subscr_user.deleted = 0 AND subscr_user.subscriber_id = subscriber.id;

ALTER TABLE portal.v_full_user_info
  OWNER TO dbuser1;
GRANT ALL ON TABLE portal.v_full_user_info TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal.v_full_user_info TO portal_role;
GRANT SELECT ON TABLE portal.v_full_user_info TO ro_role;
GRANT SELECT ON TABLE portal.v_full_user_info TO cab_role;

