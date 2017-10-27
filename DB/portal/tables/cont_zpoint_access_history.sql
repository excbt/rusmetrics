drop table portal.cont_zpoint_access_history;

/*==============================================================*/
/* Table: cont_zpoint_access_history                            */
/*==============================================================*/
create table portal.cont_zpoint_access_history (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   subscriber_id        bigint not null references subscriber(id),
   cont_zpoint_id       bigint not null references cont_zpoint(id),
   grant_date           DATE NOT NULL DEFAULT now(),
   grant_time           time NOT NULL DEFAULT now(),
   grant_tz		timestamp with time zone NOT NULL DEFAULT now(),
   revoke_date          DATE,
   revoke_time          time,
   revoke_tz		timestamp with time zone
);

comment on table portal.cont_zpoint_access_history is
'История установки доступа к точке учета';