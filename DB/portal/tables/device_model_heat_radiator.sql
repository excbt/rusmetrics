
/*==============================================================*/
/* Table: device_model_heat_radiator                            */
/*==============================================================*/
create table portal.device_model_heat_radiator (
   device_model_id      bigint not null references device_model(id),
   heat_radiator_type_id bigint not null references portal.heat_radiator_type(id),
   kc                   numeric (12,4) not null default 0
);

comment on table portal.device_model_heat_radiator is
'Связка модель прибора - тип радиатора отопления';