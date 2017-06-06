CREATE TABLE portal.cont_service_data_impulse_import
(
  id bigint default nextval('seq_global_id')  PRIMARY KEY,
  data_date timestamp without time zone,
  cont_zpoint_id bigint NOT NULL,
  device_object_id bigint,
  time_detail_type text NOT NULL,
  data_value numeric,
  data_raw numeric,
  version integer NOT NULL DEFAULT 0,
  deleted integer NOT NULL DEFAULT 0,
  created_date timestamp without time zone NOT NULL DEFAULT now(),
  last_modified_date timestamp without time zone,
  created_by bigint,
  last_modified_by bigint
)