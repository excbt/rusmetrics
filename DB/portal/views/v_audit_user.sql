-- View: audit_user

-- DROP VIEW audit_user;

CREATE OR REPLACE VIEW portal.v_audit_user AS 
 SELECT system_user.id,
    system_user.user_name,
    system_user.version,
    true AS is_system
   FROM system_user
  WHERE system_user.id > 0 AND system_user.deleted = 0
UNION
 SELECT subscr_user.id,
    subscr_user.user_name,
    subscr_user.version,
    false AS is_system
   FROM subscr_user
  WHERE subscr_user.id > 0 AND subscr_user.deleted = 0;

ALTER TABLE portal.v_audit_user
  OWNER TO dbuser1;
GRANT ALL ON TABLE portal.v_audit_user TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal.v_audit_user TO portal_role;
GRANT SELECT ON TABLE portal.v_audit_user TO ro_role;
GRANT SELECT ON TABLE portal.v_audit_user TO cabinet_role;
