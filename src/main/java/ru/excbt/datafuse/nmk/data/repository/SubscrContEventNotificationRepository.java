package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;

/**
 * Repository для SubscrContEventNotification
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
public interface SubscrContEventNotificationRepository extends PagingAndSortingRepository<SubscrContEventNotification, Long>,
		        QuerydslPredicateExecutor<SubscrContEventNotification> {

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query("SELECT count(1) FROM SubscrContEventNotification cen "
			+ "WHERE cen.subscriberId = :subscriberId AND cen.contObjectId = :contObjectId AND "
			+ " cen.contEventTime >= :dateFrom AND " + " cen.contEventTime <= :dateTo ")
	public Long selectNotificatoinsCount(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = " SELECT cen.contObjectId, count(1) FROM SubscrContEventNotification cen "
			+ " WHERE cen.subscriberId = :subscriberId AND " + " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo AND " + " cen.contObjectId IN (:contObjectIds) "
			+ " GROUP BY cen.contObjectId ")
	public List<Object[]> selectContObjectNotificatoinsCountList(@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @param isNew
	 * @return
	 */
	@Query("SELECT count(1) FROM SubscrContEventNotification cen "
			+ "WHERE cen.subscriberId = :subscriberId AND cen.contObjectId = :contObjectId AND "
			+ " cen.isNew = :isNew AND" + " cen.contEventTime >= :dateFrom AND " + " cen.contEventTime <= :dateTo ")
	public Long selectNotificatoinsCount(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
			@Param("isNew") Boolean isNew);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @param isNew
	 * @return
	 */
	@Query(value = " SELECT cen.contObjectId, count(1) FROM SubscrContEventNotification cen "
			+ " WHERE cen.subscriberId = :subscriberId AND " + " cen.isNew = :isNew AND"
			+ " cen.contEventTime >= :dateFrom AND " + " cen.contEventTime <= :dateTo AND "
			+ " cen.contObjectId IN (:contObjectIds) " + " GROUP BY cen.contObjectId ")
	public List<Object[]> selectContObjectNotificatoinsCountList(@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo, @Param("isNew") Boolean isNew);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = "SELECT cen.contEventTypeId, count(1) FROM SubscrContEventNotification cen "
			+ "WHERE cen.subscriberId = :subscriberId AND cen.contObjectId = :contObjectId AND "
			+ " cen.contEventTime >= :dateFrom AND " + " cen.contEventTime <= :dateTo "
			+ " GROUP BY cen.contEventTypeId")
	public List<Object[]> selectNotificationEventTypeCount(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = "SELECT cont_event_type_id, sum(count_events) as count_events FROM ( "
			+ " SELECT pre1.cont_object_id, CASE WHEN et.is_base_event=false THEN et.reverse_id ELSE et.id END AS cont_event_type_id, count_events "
			+ " FROM  ( " + " SELECT cen.cont_object_id, cen.cont_event_type_id, COUNT(1) count_events "
			+ " FROM subscr_cont_event_notification cen "
			+ " WHERE cen.subscriber_id = :subscriberId AND cen.cont_object_id = :contObjectId AND "
			+ " cen.cont_event_time >= :dateFrom AND " + " cen.cont_event_time <= :dateTo "
			+ " GROUP BY cen.cont_object_id, cen.cont_event_type_id " + " ) pre1, cont_event_type et "
			+ " WHERE pre1.cont_event_type_id = et.id " + ") pre2 GROUP BY cont_event_type_id", nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountCollapse(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 *
	 * select cont_event_type_id, sum(count_events) as count_events from
	 * (
	 * select pre1.cont_object_id,
	 * CASE
	 * WHEN et.is_base_event=false THEN et.reverse_id
	 * ELSE et.id
	 * END as cont_event_type_id, count_events
	 * from (
	 * select n.cont_object_id, n.cont_event_type_id, count(1) count_events
	 * from subscr_cont_event_notification n
	 * where n.cont_object_id = 20118695 and
	 * n.subscriber_id = 728 and
	 * n.cont_event_time > now() - interval '7 days'
	 * Group by n.cont_object_id, n.cont_event_type_id
	 * ) pre1, cont_event_type et
	 * WHERE pre1.cont_event_type_id = et.id
	 * ) pre2
	 * group by cont_event_type_id
	 *
	 */

	/**
	 *
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = "SELECT cen.contEventTypeId FROM SubscrContEventNotification cen "
			+ "WHERE cen.subscriberId = :subscriberId AND cen.contObjectId = :contObjectId AND "
			+ " cen.contEventTime >= :dateFrom AND " + " cen.contEventTime <= :dateTo "
			+ " GROUP BY cen.contEventTypeId")
	public List<Object[]> selectNotificationEventTypeCountGroup(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = " SELECT x.cont_object_id, COUNT(cont_event_type_id) cnt FROM ( SELECT cen.cont_object_id, cen.cont_event_type_id "
			+ " FROM subscr_cont_event_notification cen " + " WHERE cen.subscriber_id = :subscriberId AND "
			+ " cen.cont_object_id IN (:contObjectIds) AND " + " cen.cont_event_time >= :dateFrom AND "
			+ " cen.cont_event_time <= :dateTo " + " GROUP BY cen.cont_object_id, cen.cont_event_type_id) x "
			+ " GROUP BY x.cont_object_id ", nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountGroup(@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = " SELECT cont_object_id, count(cont_event_type_id) FROM ( "
			+ " SELECT DISTINCT pre1.cont_object_id, "
			+ " CASE WHEN et.is_base_event=false THEN et.reverse_id ELSE et.id END as cont_event_type_id "
			+ " FROM ( SELECT cen.cont_object_id, cen.cont_event_type_id " + " FROM subscr_cont_event_notification cen "
			+ " WHERE cen.subscriber_id = :subscriberId AND " + " cen.cont_object_id IN (:contObjectIds) AND "
			+ " cen.cont_event_time >= :dateFrom AND " + " cen.cont_event_time <= :dateTo "
			+ " GROUP BY cen.cont_object_id, cen.cont_event_type_id	" + " ) pre1, cont_event_type et "
			+ " WHERE pre1.cont_event_type_id = et.id " + " ) pre2 " + " GROUP BY cont_object_id	",
			nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountGroupCollapse(@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo);

	/*

	SELECT cont_object_id, count(1) FROM
	(
	select distinct pre1.cont_object_id,
			CASE
				WHEN et.is_base_event=false THEN et.reverse_id
				ELSE et.id
			END as cont_event_type_id
	from (
		select n.cont_object_id, n.cont_event_type_id
		from subscr_cont_event_notification n
		where 	n.cont_object_id IN (
				select sco.cont_object_id
				from subscr_cont_object sco
				where sco.subscriber_id = 728) and
			n.subscriber_id = 728 and
			n.cont_event_time > now() -  interval '7 days'
		Group by n.cont_object_id, n.cont_event_type_id
	) pre1, cont_event_type et
	WHERE pre1.cont_event_type_id = et.id
	) pre2
	GROUP BY cont_object_id
	 */

	/**
	 *
	 * @param subscriberId
	 * @param subscrUserId
	 */
	@Modifying
	@Query(value = "UPDATE subscr_cont_event_notification "
			+ " SET is_new = false, revision_time = LOCALTIMESTAMP, revision_time_tz = now(), revision_subscr_user_id = :subscrUserId "
			+ " WHERE subscriber_id = :subscriberId AND is_new = true", nativeQuery = true)
	public void updateAllSubscriberRevisions(@Param("subscriberId") Long subscriberId,
			@Param("subscrUserId") Long subscrUserId);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param subscrUserId
	 */
	@Modifying
	@Query(value = "UPDATE subscr_cont_event_notification "
			+ " SET is_new = false, revision_time = LOCALTIMESTAMP, revision_time_tz = now(), revision_subscr_user_id = :subscrUserId "
			+ " WHERE subscriber_id = :subscriberId AND is_new = true AND cont_object_id in (:contObjectIds)",
			nativeQuery = true)
	public void updateAllSubscriberRevisions(@Param("subscriberId") Long subscriberId,
			@Param("subscrUserId") Long subscrUserId, @Param("contObjectIds") List<Long> contObjectIds);

	/**
	 *
	 * @param subscriberId
	 * @param contObjectIds
	 * @param contEventTypeIds
	 * @param subscrUserId
	 */
	@Modifying
	@Query(value = "UPDATE subscr_cont_event_notification "
			+ " SET is_new = false, revision_time = LOCALTIMESTAMP, revision_time_tz = now(), revision_subscr_user_id = :subscrUserId "
			+ " WHERE subscriber_id = :subscriberId AND is_new = true AND cont_object_id in (:contObjectIds)"
			+ " AND cont_event_type_id IN (:contEventTypeIds)", nativeQuery = true)
	public void updateAllSubscriberRevisions(@Param("subscriberId") Long subscriberId,
			@Param("subscrUserId") Long subscrUserId, @Param("contObjectIds") List<Long> contObjectIds,
			@Param("contEventTypeIds") List<Long> contEventTypeIds);

	/**
	 *
	 * @param subscriberId
	 * @param subscrUserId
	 * @param oldIsNew
	 * @param isNew
	 */
	@Modifying
	@Query(value = "UPDATE subscr_cont_event_notification "
			+ " SET is_new = :isNew, revision_time = LOCALTIMESTAMP, revision_time_tz = now(), "
			+ " revision_subscr_user_id = :subscrUserId "
			+ " WHERE subscriber_id = :subscriberId AND id IN (:notificationIds)", nativeQuery = true)
	public void updateSubscriberRevisions(@Param("subscriberId") Long subscriberId,
			@Param("subscrUserId") Long subscrUserId, @Param("notificationIds") List<Long> notificationIds,
			@Param("isNew") Boolean isNew);

}
