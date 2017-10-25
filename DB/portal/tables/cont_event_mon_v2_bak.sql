CREATE TABLE portal.cont_event_mon_v2_bak
(
  id serial NOT NULL,
  cont_event_id bigint,
  reg_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT cont_event_mon_v2_bak_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE portal.cont_event_mon_v2_bak
  OWNER TO dbuser1;
GRANT ALL ON TABLE portal.cont_event_mon_v2_bak TO dbuser1;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE portal.cont_event_mon_v2_bak TO portal_role;
GRANT SELECT ON TABLE portal.cont_event_mon_v2_bak TO ro_role;
GRANT SELECT, REFERENCES, TRIGGER ON TABLE portal.cont_event_mon_v2_bak TO cabinet_role;
COMMENT ON TABLE portal.cont_event_mon_v2_bak
  IS 'Вспомогательная таблица для сохранения событий монитора первой версии';
