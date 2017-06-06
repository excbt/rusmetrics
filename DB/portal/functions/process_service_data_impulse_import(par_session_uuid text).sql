-- Function: portal.process_service_data_impulse_import(text)

-- DROP FUNCTION portal.process_service_data_impulse_import(text);

CREATE OR REPLACE FUNCTION portal.process_service_data_impulse_import(par_session_uuid text)
  RETURNS boolean AS
$BODY$
declare
        sessionUUID text = par_session_uuid;
        imp record;
        haveUpdate boolean = false;
begin
    for imp in (

	SELECT id, data_date, cont_zpoint_id, device_object_id, time_detail_type, 
		data_value, data_raw, version, deleted, created_date, last_modified_date, 
		created_by, last_modified_by, trx_id
        FROM portal.cont_service_data_impulse_import
        where trx_id::text = sessionUUID)
    loop
	INSERT INTO portal.cont_service_data_impulse(
			id, data_date, cont_zpoint_id, device_object_id, time_detail_type, 
			data_value, data_raw, version, deleted, created_date, last_modified_date, 
			created_by, last_modified_by)
         VALUES (imp.id, imp.data_date, imp.cont_zpoint_id, imp.device_object_id, imp.time_detail_type, 
		imp.data_value, imp.data_raw, imp.version, imp.deleted, imp.created_date, imp.last_modified_date, 
		imp.created_by, imp.last_modified_by);

	haveUpdate := true;
    end loop;
--    delete 
--    FROM portal.cont_service_data_hwater_import
--    where trx_id::text = sessionUUID;
    return haveUpdate;     
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION portal.process_service_data_impulse_import(text)
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.process_service_data_impulse_import(text) TO public;
GRANT EXECUTE ON FUNCTION portal.process_service_data_impulse_import(text) TO dbuser1;
