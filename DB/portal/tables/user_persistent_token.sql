CREATE TABLE portal.user_persistent_token
(
  series character varying(76) NOT NULL PRIMARY KEY,
  user_id bigint,
  token_value character varying(76) NOT NULL,
  token_date date,
  ip_address character varying(39)
)
WITH (
  OIDS=FALSE
);
