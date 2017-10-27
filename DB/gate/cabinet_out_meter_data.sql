
/*==============================================================*/
/* Table: cabinet_out_meter_data                                */
/*==============================================================*/
create table gate.cabinet_out_meter_data (
   id                   bigint primary key,
   device_object_id     bigint ,
   meter_period_id      bigint ,
   period_year          integer,
   period_mon           integer,
   meter_value_type     VARCHAR(20)          null,
   meter_data           numeric(12,2)        null,
   cont_service_type    text,
   user_id              bigint,
   user_name            TEXT                 null,
   out_datetime         timestamp with time zone,
   success              BOOL                 null
);

comment on table gate.cabinet_out_meter_data is
'Выходные данные из кабинета';
