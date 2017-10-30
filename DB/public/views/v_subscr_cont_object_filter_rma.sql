-- View: v_subscr_cont_object_filter_rma

-- DROP VIEW v_subscr_cont_object_filter_rma;

CREATE OR REPLACE VIEW v_subscr_cont_object_filter_rma AS 
SELECT a.id,
    a.subscriber_id,
    a.cont_object_id,
    a.subscr_begin_date,
    a.subscr_end_date,
    a.is_rma,
    a.rma_subscriber_id
   FROM v_subscr_cont_object_active a
  WHERE a.is_rma = false OR (a.cont_object_id IN ( SELECT b.cont_object_id
           FROM v_subscr_cont_object_active b
          WHERE b.is_rma = false AND b.rma_subscriber_id = a.subscriber_id));

ALTER TABLE v_subscr_cont_object_filter_rma
  OWNER TO dbuser1;
GRANT ALL ON TABLE v_subscr_cont_object_filter_rma TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_subscr_cont_object_filter_rma TO portal_role;
GRANT SELECT ON TABLE v_subscr_cont_object_filter_rma TO ro_role;
GRANT SELECT ON TABLE v_subscr_cont_object_filter_rma TO cabinet_role;
