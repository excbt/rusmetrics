/*==============================================================*/
/* Table: energy_passport_data                                  */
/*==============================================================*/
create table portal.energy_passport_data (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   passport_id          bigint  not null references portal.energy_passport(id),
   section_id           bigint,
   section_key          TEXT NOT null,
   section_data_json    jsonb,
   section_entry_id
);

comment on table portal.energy_passport_data is
'Данные энергетического паспорта по разделам';