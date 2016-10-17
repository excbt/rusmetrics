package ru.excbt.datafuse.nmk.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;

/**
 * Repository для ContEvent
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.02.2015
 *
 */
public interface ContEventRepository extends PagingAndSortingRepository<ContEvent, Long> {

	public List<ContEvent> findByContObjectId(Long contObjectId, Pageable pageable);

	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObjectId IN (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")
	public List<ContEvent> selectBySubscriber(@Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObjectId IN (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")
	public Page<ContEvent> selectBySubscriberPage(@Param("subscriberId") long subscriberId, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObjectId IN (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")
	public Page<ContEvent> selectBySubscriber(@Param("subscriberId") long subscriberId, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce "
			+ " WHERE ce.contObjectId IN ( SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId  ) "
			+ "AND ce.eventTime >= :eventStartDate AND ce.eventTime <= :eventEndDate " + " ORDER BY ce.eventTime DESC ")
	public Page<ContEvent> selectBySubscriberAndDate(@Param("subscriberId") long subscriberId,
			@Param("eventStartDate") Date eventStartDate, @Param("eventEndDate") Date eventEndDate, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce " + "WHERE ce.contObjectId IN " + "(SELECT sco.contObjectId "
			+ " FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.contObjectId IN (:contObjectIds)) "
			+ "ORDER BY ce.eventTime DESC")
	public Page<ContEvent> selectBySubscriberAndContObjects(@Param("subscriberId") long subscriberId,
			@Param("contObjectIds") List<Long> contObjectIds, Pageable pageable);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT ce FROM ContEvent ce WHERE ce.contObjectId IN " + "(SELECT sco.contObjectId "
			+ " FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.id IN (:contObjectIds) ) AND "
			+ " ce.eventTime >= :eventStartDate AND ce.eventTime <= :eventEndDate " + " ORDER BY ce.eventTime DESC")
	public Page<ContEvent> selectBySubscriberAndDateAndContObjects(@Param("subscriberId") long subscriberId,
			@Param("eventStartDate") Date eventStartDate, @Param("eventEndDate") Date eventEndDate,
			@Param("contObjectIds") List<Long> contObjectIds, Pageable pageable);

	/**
	 * @param contEventsIds
	 * @return
	 */
	@Query("SELECT ce FROM ContEvent ce WHERE ce.id IN (:contEventsIds)")
	public List<ContEvent> selectContEventsByIds(@Param("contEventsIds") Collection<Long> contEventsIds);

}
