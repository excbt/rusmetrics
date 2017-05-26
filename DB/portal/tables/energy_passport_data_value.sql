--drop table portal.energy_passport_data_value;

/*==============================================================*/
/* Table: energy_passport_data_value                            */
/*==============================================================*/
create table portal.energy_passport_data_value (
   passport_id          bigint not null REFERENCES portal.energy_passport(id),
   section_key          TEXT  NOT null,
   complex_idx          TEXT  NOT null,
   section_entry_id     bigint not null,
   data_value           TEXT,
   data_type            TEXT NOT null,
   CONSTRAINT energy_passport_data_value_pkey PRIMARY KEY (passport_id, section_key, section_entry_id, complex_idx),
);

comment on table portal.energy_passport_data_value is
'Развернутая информация значений из section_data_json';