package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " cen.isNew = :isNew")
	public Page<SubscrContEventNotification> selectAll(
			@Param("subscriberId") long subscriberId,
			@Param("isNew") boolean isNew, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId ")
	public Page<SubscrContEventNotification> selectAll(
			@Param("subscriberId") long subscriberId, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param fromDate
	 * @param toDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " cen.contEventTime >= :fromDate AND cen.contEventTime <= :toDate AND"
			+ " cen.isNew = :isNew")
	public Page<SubscrContEventNotification> selectByDate(
			@Param("subscriberId") long subscriberId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("isNew") boolean isNew, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param fromDate
	 * @param toDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " cen.contEventTime >= :fromDate AND cen.contEventTime <= :toDate ")
	public Page<SubscrContEventNotification> selectByDate(
			@Param("subscriberId") long subscriberId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param eventStartDate
	 * @param eventEndDate
	 * @param contObjectIds
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " cen.contEventTime >= :fromDate AND cen.contEventTime <= :toDate AND"
			+ " cen.contObjectId IN  (:contObjectIds) AND "
			+ " cen.isNew = :isNew ")
	public Page<SubscrContEventNotification> selectByDateContObjectIds(
			@Param("subscriberId") long subscriberId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("isNew") boolean isNew, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param fromDate
	 * @param toDate
	 * @param contObjectIds
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " cen.contEventTime >= :fromDate AND cen.contEventTime <= :toDate AND"
			+ " cen.contObjectId IN  (:contObjectIds) ")
	public Page<SubscrContEventNotification> selectByDateContObjectIds(
			@Param("subscriberId") long subscriberId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
			@Param("contObjectIds") List<Long> contObjectIds, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param eventStartDate
	 * @param eventEndDate
	 * @param contObjectIds
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " ce.contObject.id IN (:contObjectIds) AND"
			+ " cen.isNew = :isNew ")
	public Page<SubscrContEventNotification> selectByContObjects(
			@Param("subscriberId") long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds,
			@Param("isNew") Boolean isNew, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectIds
	 * @param isNew
	 * @param pageable
	 * @return
	 */
	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN cen.contEvent ce LEFT JOIN ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId AND "
			+ " ce.contObject.id IN (:contObjectIds) ")
	public Page<SubscrContEventNotification> selectByContObjects(
			@Param("subscriberId") long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, Pageable pageable);

}
