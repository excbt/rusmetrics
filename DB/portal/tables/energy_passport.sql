drop table if exists portal.energy_passport cascade;
/*==============================================================*/
/* Table: energy_passport                                       */
/*==============================================================*/
create table portal.energy_passport (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   subscriber_id        bigint not null references subscriber(id),
   passport_template_id bigint not null references portal.energy_passport_template(id),
   passport_name        TEXT,
   passport_date        DATE,
   description          TEXT                 null,
   comment              TEXT                 null,
   organization_id      bigint references organization(id),
   document_mode 		integer NOT NULL DEFAULT 1,
   passport_state 		integer
);

comment on table portal.energy_passport is
'Энергетический паспорт';
