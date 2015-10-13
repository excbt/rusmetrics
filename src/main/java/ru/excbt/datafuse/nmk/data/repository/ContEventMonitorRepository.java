package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;

public interface ContEventMonitorRepository extends JpaRepository<ContEventMonitor, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEventMonitor> findByContObjectId(Long contObjectId);

	@Query("SELECT m FROM ContEventMonitor m " + "WHERE m.contObjectId = :contObjectId " + " ORDER BY m.contEventTime ")
	public List<ContEventMonitor> selectByContObjectId(@Param("contObjectId") Long contObjectId);

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT m FROM ContEventMonitor m " + "WHERE m.contObjectId IN (:contObjectIds)")
	public List<ContEventMonitor> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT m FROM ContEventMonitor m " + "WHERE m.contObjectId IN ( "
			+ "SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId)")
	public List<ContEventMonitor> selectBySubscriberId(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query(value = "SELECT CAST(f.city_fias_uuid as TEXT) city_fias_uuid, COUNT(cet.cont_event_id) cont_object_count "
			+ "FROM cont_event_monitor cet, cont_object_fias f, subscr_cont_object sco "
			+ "WHERE cet.cont_object_id = f.cont_object_id " + "AND cet.cont_object_id = sco.cont_object_id "
			+ "AND sco.subscriber_id = :subscriberId " + "GROUP BY city_fias_uuid ", nativeQuery = true)
	public List<Object[]> selectCityContObjectMonitorEventCount(@Param("subscriberId") Long subscriberId);

}
