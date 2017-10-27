/*==============================================================*/
/* Table: device_type                                           */
/*==============================================================*/
create table portal.device_type (
   keyname              text primary key,
   type_name            TEXT                 null,
   caption              TEXT                 null,
   enabled              BOOL                 null
);

comment on table portal.device_type is
'Тип прибора';