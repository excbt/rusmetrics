drop table portal.cont_object_access_history;

create table portal.cont_object_access_history (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   subscriber_id        bigint not null references subscriber(id),
   cont_object_id       bigint not null references cont_object(id),
   grant_date date NOT NULL DEFAULT now(),
   grant_time time NOT NULL DEFAULT now(),
   grant_tz timestamp with time zone NOT NULL DEFAULT now(),
   revoke_date date,
   revoke_time time,
   revoke_tz timestamp with time zone
);

comment on table portal.cont_object_access_history is
'Доступ к объекту учета. История';