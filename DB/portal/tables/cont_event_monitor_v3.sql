-- Table: portal.cont_event_monitor_v3

-- DROP TABLE portal.cont_event_monitor_v3;

CREATE TABLE portal.cont_event_monitor_v3
(
  id bigint NOT NULL DEFAULT nextval('seq_global_id'::regclass),
  cont_object_id bigint NOT NULL,
  cont_zpoint_id bigint NOT NULL,
  cont_event_id bigint NOT NULL,
  cont_event_type_id bigint NOT NULL,
  cont_event_time timestamp without time zone NOT NULL,
  cont_event_level integer,
  cont_event_level_color text,
  last_cont_event_id bigint,
  last_cont_event_time timestamp without time zone,
  monitor_time timestamp without time zone NOT NULL DEFAULT now(),
  monitor_time_tz timestamp with time zone NOT NULL DEFAULT now(),
  worse_cont_event_id bigint,
  worse_cont_event_time timestamp without time zone,
  is_scalar boolean,
  monitor_version smallint NOT NULL DEFAULT 3,
  CONSTRAINT cont_event_monitor_v3_pkey PRIMARY KEY (id),
  CONSTRAINT cont_event_monitor_v3_cont_object_id_cont_event_type_id_key UNIQUE (cont_object_id, cont_event_type_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE portal.cont_event_monitor_v3
  OWNER TO dbuser1;
GRANT ALL ON TABLE portal.cont_event_monitor_v3 TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal.cont_event_monitor_v3 TO portal_role;
GRANT SELECT ON TABLE portal.cont_event_monitor_v3 TO ro_role;
GRANT SELECT, REFERENCES, TRIGGER ON TABLE portal.cont_event_monitor_v3 TO cabinet_role;

-- Index: portal.cont_event_monitor_v3_cont_event_id_idx

-- DROP INDEX portal.cont_event_monitor_v3_cont_event_id_idx;

CREATE INDEX cont_event_monitor_v3_cont_event_id_idx
  ON portal.cont_event_monitor_v3
  USING btree
  (cont_event_id);

-- Index: portal.cont_event_monitor_v3_cont_object_id_idx

-- DROP INDEX portal.cont_event_monitor_v3_cont_object_id_idx;

CREATE INDEX cont_event_monitor_v3_cont_object_id_idx
  ON portal.cont_event_monitor_v3
  USING btree
  (cont_object_id);

