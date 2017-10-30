do $$DECLARE
begin

----==========================================================
delete from portal.cont_zpoint_access;

INSERT INTO portal.cont_zpoint_access(
            subscriber_id, cont_zpoint_id)
select sco.subscriber_id, zp.id as cont_zpoint_id

from (
	SELECT subscriber_id, cont_object_id
	FROM subscr_cont_object 
	where subscr_end_date is null and deleted = 0) sco LEFT join (select * FROM cont_zpoint WHERE deleted = 0) zp ON (sco.cont_object_id = zp.cont_object_id)
where zp.id is not null;

---
delete from portal.cont_object_access_history;
INSERT INTO portal.cont_object_access_history(
            subscriber_id, cont_object_id, grant_date, 
            grant_time, 
            grant_tz, 
            revoke_date, 
            revoke_time, 
            revoke_tz, 
            version, deleted, created_date, last_modified_date, created_by, last_modified_by)
SELECT 	subscriber_id, cont_object_id, coalesce(date_trunc('month', subscr_begin_date)::date, created_date::date) as grant_date, 
	coalesce((subscr_begin_date - date_trunc('month', subscr_begin_date))::time, created_date::time) as grant_time,
	coalesce(subscr_begin_date::timestamptz, created_date::timestamptz) as grant_tz,
	date_trunc('month', subscr_end_date)::date as revoke_date,
	(subscr_end_date - date_trunc('month', subscr_end_date))::time as revoke_time,
	subscr_begin_date::timestamptz as revoke_tz,
	version, deleted, created_date, last_modified_date, created_by, last_modified_by
FROM subscr_cont_object ;

----==========================================================
delete from portal.cont_object_access;

INSERT INTO portal.cont_object_access(
            subscriber_id, cont_object_id)
            
select sco.subscriber_id, cont_object_id
from (
	SELECT subscriber_id, cont_object_id
	FROM subscr_cont_object 
	where subscr_end_date is null and deleted = 0) sco LEFT join (select * FROM cont_object WHERE deleted = 0) co ON (sco.cont_object_id = co.id)
where co.id is not null;

----
delete from portal.cont_zpoint_access_history;

INSERT INTO portal.cont_zpoint_access_history(
            subscriber_id, cont_zpoint_id, grant_date, grant_time, grant_tz, 
            revoke_date, revoke_time, revoke_tz, version, deleted, created_date, 
            last_modified_date, created_by, last_modified_by)
            
select 
	sco.subscriber_id, zp.id as cont_zpoint_id, sco.grant_date, sco.grant_time, sco.grant_tz, 
        sco.revoke_date, sco.revoke_time, sco.revoke_tz, sco.version, sco.deleted, sco.created_date, 
        sco.last_modified_date, sco.created_by, sco.last_modified_by

from (

SELECT 	subscriber_id, cont_object_id, coalesce(date_trunc('month', subscr_begin_date)::date, created_date::date) as grant_date, 
	coalesce((subscr_begin_date - date_trunc('month', subscr_begin_date))::time, created_date::time) as grant_time,
	coalesce(subscr_begin_date::timestamptz, created_date::timestamptz) as grant_tz,
	date_trunc('month', subscr_end_date)::date as revoke_date,
	(subscr_end_date - date_trunc('month', subscr_end_date))::time as revoke_time,
	subscr_begin_date::timestamptz as revoke_tz,
	version, deleted, created_date, last_modified_date, created_by, last_modified_by
FROM subscr_cont_object ) sco LEFT join (select * FROM cont_zpoint WHERE deleted = 0) zp ON (sco.cont_object_id = zp.cont_object_id)
where zp.id is not null;

---==============================================================


end$$;