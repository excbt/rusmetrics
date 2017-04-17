drop table if exists portal.energy_passport_section;
/*==============================================================*/
/* Table: energy_passport_section                               */
/*==============================================================*/
create table portal.energy_passport_section (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   passport_id          bigint not null references portal.energy_passport(id),
   section_key          TEXT,
   section_json         JSONB,
   section_order        integer,
   enabled              boolean not null default true,
   has_entries          boolean not null default false
);

comment on table portal.energy_passport_section is
'Раздел энергетического паспорта';