/*==============================================================*/
/* Table: energy_passport_section_templat                       */
/*==============================================================*/

DROP table portal.energy_passport_section_template;

create table portal.energy_passport_section_template (
   id                   BIGINT DEFAULT nextval('seq_global_id') PRIMARY KEY,
   passport_template_id BIGINT REFERENCES portal.energy_passport_template(id),
   section_key          TEXT                 not null,
   section_json         jsonb,
   section_order        int
);

comment on table portal.energy_passport_section_template is
'Шаблон раздела энергетического паспорта';