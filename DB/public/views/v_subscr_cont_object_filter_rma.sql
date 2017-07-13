-- View: v_subscr_cont_object_filter_rma

-- DROP VIEW v_subscr_cont_object_filter_rma;

CREATE OR REPLACE VIEW v_subscr_cont_object_filter_rma AS 
 SELECT sco.id,
    sco.subscriber_id,
    sco.cont_object_id,
    sco.subscr_begin_date,
    sco.subscr_end_date,
    sco.is_rma,
    sco.rma_subscriber_id
   FROM v_subscr_cont_object_active sco
  WHERE sco.is_rma = false OR (sco.cont_object_id IN ( SELECT co2.cont_object_id
           FROM v_subscr_cont_object_active co2
          WHERE co2.is_rma = false AND co2.rma_subscriber_id = sco.subscriber_id));

ALTER TABLE v_subscr_cont_object_filter_rma
  OWNER TO dbuser1;
GRANT ALL ON TABLE v_subscr_cont_object_filter_rma TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_subscr_cont_object_filter_rma TO portal_role;
GRANT SELECT ON TABLE v_subscr_cont_object_filter_rma TO ro_role;
GRANT SELECT ON TABLE v_subscr_cont_object_filter_rma TO cabinet_role;
