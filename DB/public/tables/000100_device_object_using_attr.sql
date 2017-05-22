ALTER TABLE device_object
  ADD COLUMN using_attr character varying(1);
ALTER TABLE device_object
  ADD CHECK (using_attr in ('P','S'));
COMMENT ON COLUMN device_object.using_attr IS 'Признак инстанса прибора учета – Коллективный или общедомовой
S - коллективный
P - персональный';
