/*==============================================================*/
/* Table: energy_passport_data                                  */
/*==============================================================*/
create table portal.energy_passport_data (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   passport_id          bigint  not null references portal.energy_passport(id),
   section_key          TEXT NOT null,
   section_data_json    jsonb
);

comment on table portal.energy_passport_data is
'Данные энергетического паспорта по разделам';