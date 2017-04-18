--DROP FUNCTION portal.update_mv_last_data_date_aggr();

CREATE OR REPLACE FUNCTION portal.update_mv_last_data_date_aggr()
  RETURNS boolean AS
$BODY$
declare
v_adv_lock                      boolean;

	--v_process_limit integer = 10;
begin
v_adv_lock := pg_try_advisory_xact_lock(hashtext('portal.update_mv_last_data_date_aggr'));
IF v_adv_lock = 'false' THEN
	RAISE NOTICE 'Portal cont_zpoint_check_no_data already running.';
	RETURN false;
END IF;

    REFRESH MATERIALIZED VIEW CONCURRENTLY portal.mv_last_data_date_aggr;

    RETURN true;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
 ALTER FUNCTION portal.cont_zpoint_check_no_data()
  OWNER TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO public;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO dbuser1;
GRANT EXECUTE ON FUNCTION portal.cont_zpoint_check_no_data() TO portal_role; 