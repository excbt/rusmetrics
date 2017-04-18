DO $$
--DECLARE v_List TEXT;
BEGIN
  --portal.update_mv_last_data_date_aggr();
  perform portal.cont_zpoint_check_no_data();
  perform portal.register_last_cont_event();
  RAISE EXCEPTION 'HALLO';
END $$;