-- Table: portal.cont_event_monitor_history_v3

-- DROP TABLE portal.cont_event_monitor_history_v3;

CREATE TABLE portal.cont_event_monitor_history_v3
(
  id bigint NOT NULL DEFAULT nextval('seq_global_id'::regclass),
  cont_event_id bigint NOT NULL,
  reverse_cont_event_id bigint NOT NULL,
  cont_event_level_color text,
  monitor_time timestamp without time zone NOT NULL,
  monitor_time_tz timestamp with time zone NOT NULL,
  monitor_history_time timestamp without time zone NOT NULL DEFAULT now(),
  monitor_history_time_tz timestamp with time zone NOT NULL DEFAULT now(),
  monitor_version smallint NOT NULL DEFAULT 3,
  CONSTRAINT cont_event_monitor_history_v3_pkey PRIMARY KEY (id),
  CONSTRAINT cont_event_monitor_history_v3_cont_event_id_key UNIQUE (cont_event_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE portal.cont_event_monitor_history_v3
  OWNER TO dbuser1;
GRANT ALL ON TABLE portal.cont_event_monitor_history_v3 TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal.cont_event_monitor_history_v3 TO portal_role;
GRANT SELECT ON TABLE portal.cont_event_monitor_history_v3 TO ro_role;
GRANT SELECT, REFERENCES, TRIGGER ON TABLE portal.cont_event_monitor_history_v3 TO cabinet_role;

-- Index: portal.cont_event_monitor_history_v3_reverse_cont_event_id_idx

-- DROP INDEX portal.cont_event_monitor_history_v3_reverse_cont_event_id_idx;

CREATE INDEX cont_event_monitor_history_v3_reverse_cont_event_id_idx
  ON portal.cont_event_monitor_history_v3
  USING btree
  (reverse_cont_event_id);

