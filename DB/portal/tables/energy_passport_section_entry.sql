/*==============================================================*/
/* Table: energy_passport_section_entry                         */
/*==============================================================*/
create table portal.energy_passport_section_entry (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   section_id           bigint not null references portal.energy_passport_section(id),
   entry_name           TEXT                 null,
   entry_description    TEXT                 null,
   entry_order          int
);

comment on table portal.energy_passport_section_entry is
'Данные по записям для секций паспорта';