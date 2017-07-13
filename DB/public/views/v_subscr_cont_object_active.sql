-- View: v_subscr_cont_object_active

-- DROP VIEW v_subscr_cont_object_active;

CREATE OR REPLACE VIEW v_subscr_cont_object_active AS 
SELECT null::bigint as id,
    a.subscriber_id,
    a.cont_object_id,
    a.grant_tz::date as subscr_begin_date,
    a.revoke_tz::date as subscr_end_date,
    COALESCE(s.is_rma, false) AS is_rma,
    s.rma_subscriber_id
   FROM portal.cont_object_access a,
    subscriber s
  WHERE a.subscriber_id = s.id AND s.deleted = 0;


ALTER TABLE v_subscr_cont_object_active
  OWNER TO dbuser1;
GRANT ALL ON TABLE v_subscr_cont_object_active TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_subscr_cont_object_active TO portal_role;
GRANT SELECT ON TABLE v_subscr_cont_object_active TO ro_role;
GRANT SELECT ON TABLE v_subscr_cont_object_active TO cabinet_role;
