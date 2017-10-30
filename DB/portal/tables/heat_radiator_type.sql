/*==============================================================*/
/* Table: heat_radiator_type                                    */
/*==============================================================*/
create table portal.heat_radiator_type (
   id                   bigint default nextval('seq_global_id')  PRIMARY KEY,
   type_name                 TEXT                 not null,
   type_description          TEXT                 null
);

comment on table portal.heat_radiator_type is
'Тип радиатора отопления';
