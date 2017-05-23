ALTER TABLE device_object
  ADD COLUMN inst_type character varying(1);
ALTER TABLE device_object
  ADD CHECK (inst_type in ('P','S'));
COMMENT ON COLUMN device_object.inst_type IS 'Признак инстанса прибора учета – Коллективный или общедомовой
S - коллективный
P - персональный';
