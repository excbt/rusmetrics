-- Function: portal.f_cont_object_tariff_get(text, bigint, bigint, timestamp without time zone)

-- DROP FUNCTION portal.f_cont_object_tariff_get(text, bigint, bigint, timestamp without time zone);

CREATE OR REPLACE FUNCTION gate.cabinet_out_meter_data(
	par_device_object_id bigint,
	par_meter_period_id bigint,
	par_period_year integer,
	par_period_mon integer,
	par_meter_value_type character varying(20),
	par_meter_data numeric(12,2),
	par_cont_service_type text,
	par_user_id bigint,
	par_user_name text
    )
  RETURNS bigint AS
$BODY$
DECLARE 
	result bigint;
BEGIN

INSERT INTO gate.cabinet_out_meter_data(
            device_object_id, meter_period_id, period_year, period_mon, 
            meter_value_type, meter_data, cont_service_type, user_id, user_name, 
            out_datetime, processed)
    VALUES (par_device_object_id, par_meter_period_id, par_period_year, par_period_mon, 
            par_meter_value_type, par_meter_data, par_cont_service_type, par_user_id, par_user_name, 
            current_timestamp, false) returning id into result;

        RETURN result;
        
END;
$BODY$
  LANGUAGE plpgsql
  COST 100;