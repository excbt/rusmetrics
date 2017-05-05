--drop table portal.energy_passport_cont_object;

/*==============================================================*/
/* Table: energy_passport_cont_object                           */
/*==============================================================*/
create table portal.energy_passport_cont_object (
   passport_id          bigint not null references portal.energy_passport(id),
   cont_object_id       bigint not null references cont_object(id),
   CONSTRAINT energy_passport_cont_object_pkey PRIMARY KEY (passport_id, cont_object_id)
);

comment on table portal.energy_passport_cont_object is
'Привязка энергетического паспорта к объектам учета';
