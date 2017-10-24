CREATE OR REPLACE FUNCTION portal.mon_version_sync_trig_func()
  RETURNS trigger AS
$BODY$
BEGIN 
  IF NEW.monitor_version is null THEN 
      NEW.monitor_version = NEW.mon_version;
  ELSIF NEW.monitor_version is NOT null AND (NEW.mon_version <> NEW.monitor_version) THEN 
      NEW.mon_version = NEW.monitor_version;
  END IF; 
  RETURN NEW;
END $BODY$
LANGUAGE plpgsql VOLATILE
COST 100;