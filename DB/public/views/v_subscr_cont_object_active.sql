-- View: v_subscr_cont_object_active

-- DROP VIEW v_subscr_cont_object_active;

CREATE OR REPLACE VIEW v_subscr_cont_object_active AS 
 SELECT co.id,
    co.subscriber_id,
    co.cont_object_id,
    co.subscr_begin_date,
    co.subscr_end_date,
    COALESCE(s.is_rma, false) AS is_rma,
    s.rma_subscriber_id
   FROM subscr_cont_object co,
    subscriber s
  WHERE co.subscr_end_date IS NULL AND co.subscriber_id = s.id AND co.deleted = 0 AND s.deleted = 0;

ALTER TABLE v_subscr_cont_object_active
  OWNER TO dbuser1;
GRANT ALL ON TABLE v_subscr_cont_object_active TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_subscr_cont_object_active TO portal_role;
GRANT SELECT ON TABLE v_subscr_cont_object_active TO ro_role;
GRANT SELECT ON TABLE v_subscr_cont_object_active TO cabinet_role;
