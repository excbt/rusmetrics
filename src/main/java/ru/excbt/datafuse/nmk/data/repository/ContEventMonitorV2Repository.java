package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;

/**
 * Repository для ContEventMonitor
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.06.2015
 *
 */
public interface ContEventMonitorV2Repository extends JpaRepository<ContEventMonitorV2, Long> {

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	public List<ContEventMonitorV2> findByContObjectId(Long contObjectId);

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Query(" SELECT m FROM ContEventMonitorV2 m "
			+ " WHERE m.contObjectId = :contObjectId AND (m.isScalar IS NULL OR m.isScalar = FALSE) "
			+ " ORDER BY m.contEventTime ")
	public List<ContEventMonitorV2> selectByContObjectId(@Param("contObjectId") Long contObjectId);

	/**
	 *
	 * @param contObjectId
	 * @param contZpointId
	 * @return
	 */
	@Query(" SELECT m FROM ContEventMonitorV2 m, ContEvent ce "
			+ " WHERE m.contObjectId = :contObjectId AND (m.isScalar IS NULL OR m.isScalar = FALSE) "
			+ " AND m.contEventId = ce.id AND ce.contZPointId = :contZPointId " + " ORDER BY m.contEventTime ")
	public List<ContEventMonitorV2> selectByZPointId(@Param("contObjectId") Long contObjectId,
			@Param("contZPointId") Long contZPointId);

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Query(" SELECT m FROM ContEventMonitorV2 m " + "WHERE m.contObjectId IN (:contObjectIds) "
			+ " AND (m.isScalar IS NULL OR m.isScalar = FALSE) ")
	public List<ContEventMonitorV2> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT m FROM ContEventMonitorV2 m " + "WHERE m.contObjectId IN ( "
			+ "SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId) "
			+ "AND (m.isScalar IS NULL OR m.isScalar = FALSE)")
	public List<ContEventMonitorV2> selectBySubscriberId(@Param("subscriberId") Long subscriberId);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query(value = "SELECT CAST(f.city_fias_uuid as TEXT) city_fias_uuid, COUNT(m.cont_event_id) cont_object_count "
			+ "FROM portal.cont_event_monitor_v2 m, cont_object_fias f, subscr_cont_object sco "
			+ "WHERE m.cont_object_id = f.cont_object_id AND m.cont_object_id = sco.cont_object_id "
			+ " AND (m.is_scalar IS NULL OR m.is_scalar = FALSE) " + "AND sco.subscriber_id = :subscriberId "
			+ "GROUP BY city_fias_uuid ", nativeQuery = true)
	public List<Object[]> selectCityContObjectMonitorEventCount(@Param("subscriberId") Long subscriberId);

}
