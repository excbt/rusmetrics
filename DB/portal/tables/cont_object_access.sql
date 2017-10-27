/*==============================================================*/
/* Table: cont_object_access                                    */
/*==============================================================*/
create table portal.cont_object_access (
   subscriber_id        bigint not null references subscriber(id),
   cont_object_id       bigint not null references cont_object(id)
);

comment on table portal.cont_object_access is
'Доступ к объекту учета';
