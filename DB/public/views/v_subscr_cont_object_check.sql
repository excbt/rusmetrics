-- View: v_subscr_cont_object_check

-- DROP VIEW v_subscr_cont_object_check;

CREATE OR REPLACE VIEW v_subscr_cont_object_check AS 
 SELECT subscr_cont_object.subscriber_id,
    subscr_cont_object.cont_object_id,
    count(1) AS count_check
   FROM subscr_cont_object
  WHERE subscr_cont_object.subscr_end_date IS NULL AND subscr_cont_object.deleted = 0
  GROUP BY subscr_cont_object.subscriber_id, subscr_cont_object.cont_object_id;

ALTER TABLE v_subscr_cont_object_check
  OWNER TO dbuser1;
GRANT ALL ON TABLE v_subscr_cont_object_check TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE v_subscr_cont_object_check TO portal_role;
GRANT SELECT ON TABLE v_subscr_cont_object_check TO ro_role;
GRANT SELECT ON TABLE v_subscr_cont_object_check TO cabinet_role;
