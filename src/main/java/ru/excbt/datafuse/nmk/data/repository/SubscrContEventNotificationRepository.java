package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;

public interface SubscrContEventNotificationRepository extends
		PagingAndSortingRepository<SubscrContEventNotification, Long>,
		JpaSpecificationExecutor<SubscrContEventNotification> {

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
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo ")
	public Long selectNotificatoinsCount(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = " SELECT cen.contObjectId, count(1) FROM SubscrContEventNotification cen "
			+ " WHERE cen.subscriberId = :subscriberId AND "
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo AND "
			+ " cen.contObjectId IN (:contObjectIds) "
			+ " GROUP BY cen.contObjectId ")
	public List<Object[]> selectNotificatoinsCountList(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

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
			+ " cen.isNew = :isNew AND"
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo ")
	public Long selectNotificatoinsCount(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
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
			+ " WHERE cen.subscriberId = :subscriberId AND "
			+ " cen.isNew = :isNew AND"
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo AND "
			+ " cen.contObjectId IN (:contObjectIds) "
			+ " GROUP BY cen.contObjectId ")
	public List<Object[]> selectNotificatoinsCountList(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
			@Param("isNew") Boolean isNew);

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
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo "
			+ " GROUP BY cen.contEventTypeId")
	public List<Object[]> selectNotificationEventTypeCount(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	
	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = "SELECT cont_event_type_id, sum(count_events) as count_events FROM ( "
			+ " SELECT pre1.cont_object_id, CASE WHEN pre1.is_base_event=false THEN pre1.reverse_cont_event_type_id ELSE pre1.cont_event_type_id END AS cont_event_type_id, count_events "
			+ " FROM  ( "
			+ " SELECT cen.cont_object_id, cen.cont_event_type_id, et.is_base_event, et.reverse_id reverse_cont_event_type_id, COUNT(1) count_events "
			+ " FROM subscr_cont_event_notification cen, cont_event_type et "
			+ " WHERE cen.subscriber_id = :subscriberId AND cen.cont_object_id = :contObjectId AND "
			+ " cen.cont_event_time >= :dateFrom AND "
			+ " cen.cont_event_time <= :dateTo AND "
			+ " cen.cont_event_type_id = et.id "
			+ " GROUP BY cen.cont_object_id, cen.cont_event_type_id, et.is_base_event, et.reverse_id "
			+ " ) pre1) pre2 GROUP BY cont_event_type_id", nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountCollapse(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

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
			+ " cen.contEventTime >= :dateFrom AND "
			+ " cen.contEventTime <= :dateTo "
			+ " GROUP BY cen.contEventTypeId")
	public List<Object[]> selectNotificationEventTypeCountGroup(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") Long contObjectId,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@Query(value = " SELECT x.cont_object_id, COUNT(cont_event_type_id) cnt FROM ( SELECT cen.cont_object_id, cen.cont_event_type_id "
			+ " FROM subscr_cont_event_notification cen "
			+ " WHERE cen.subscriber_id = :subscriberId AND "
			+ " cen.cont_object_id IN (:contObjectIds) AND "
			+ " cen.cont_event_time >= :dateFrom AND "
			+ " cen.cont_event_time <= :dateTo "
			+ " GROUP BY cen.cont_object_id, cen.cont_event_type_id) x "
			+ " GROUP BY x.cont_object_id ", nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountGroup(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

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
			+ " CASE WHEN pre1.is_base_event=false THEN pre1.reverse_cont_event_type_id ELSE pre1.cont_event_type_id END as cont_event_type_id "
			+ " FROM ( SELECT cen.cont_object_id, cen.cont_event_type_id, et.is_base_event, et.reverse_id reverse_cont_event_type_id "
			+ " FROM subscr_cont_event_notification cen, cont_event_type et "
			+ " WHERE cen.subscriber_id = :subscriberId AND "
			+ " cen.cont_object_id IN (:contObjectIds) AND "
			+ " cen.cont_event_time >= :dateFrom AND "
			+ " cen.cont_event_time <= :dateTo AND"
			+ " cen.cont_event_type_id = et.id "
			+ " GROUP BY cen.cont_object_id, cen.cont_event_type_id	, et.is_base_event, et.reverse_id "
			+ " ) pre1 "
			+ " ) pre2 "
			+ " GROUP BY cont_object_id	", nativeQuery = true)
	public List<Object[]> selectNotificationEventTypeCountGroupCollapse(
			@Param("subscriberId") Long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

}
