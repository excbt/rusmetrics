--drop table portal.energy_passport_data_value;

/*==============================================================*/
/* Table: energy_passport_data_value                            */
/*==============================================================*/
create table portal.energy_passport_data_value (
   passport_id          bigint not null REFERENCES portal.energy_passport(id),
   section_key          TEXT  NOT null,
   complex_idx          TEXT  NOT null,
   data_value           TEXT,
   data_type            TEXT NOT null
);

comment on table portal.energy_passport_data_value is
'Развернутая информация значений из section_data_json';