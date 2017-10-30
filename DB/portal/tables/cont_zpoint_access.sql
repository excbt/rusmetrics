/*==============================================================*/
CREATE TABLE portal.cont_zpoint_access
(
  subscriber_id bigint NOT NULL,
  cont_zpoint_id bigint NOT NULL,
  CONSTRAINT cont_zpoint_access_pkey PRIMARY KEY (subscriber_id, cont_zpoint_id),
  CONSTRAINT cont_zpoint_access_cont_zpoint_id_fkey FOREIGN KEY (cont_zpoint_id)
      REFERENCES cont_zpoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cont_zpoint_access_subscriber_id_fkey FOREIGN KEY (subscriber_id)
      REFERENCES subscriber (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)s

comment on table portal.cont_zpoint_access is
'Доступ к точке учета для абонента';
