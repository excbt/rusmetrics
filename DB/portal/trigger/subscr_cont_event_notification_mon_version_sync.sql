-- Trigger: mon_version_sync on public.subscr_cont_event_notification

-- DROP TRIGGER mon_version_sync ON public.subscr_cont_event_notification;

CREATE TRIGGER mon_version_sync
  BEFORE INSERT OR UPDATE
  ON public.subscr_cont_event_notification
  FOR EACH ROW
  EXECUTE PROCEDURE portal.mon_version_sync_trig_func();
