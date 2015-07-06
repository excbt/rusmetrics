package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;

public interface ContEventMonitorRepository extends
		JpaRepository<ContEventMonitor, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<ContEventMonitor> findByContObjectId(Long contObjectId);

	
	@Query("SELECT m FROM ContEventMonitor m "
			+ "WHERE m.contObjectId = :contObjectId "
			+ " ORDER BY m.contEventTime ")
	public List<ContEventMonitor> selectByContObjectId(
			@Param("contObjectId") Long contObjectId);

	
	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT m FROM ContEventMonitor m "
			+ "WHERE m.contObjectId IN (:contObjectIds)")
	public List<ContEventMonitor> selectByContObjectIds(
			@Param("contObjectIds") List<Long> contObjectIds);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT m FROM ContEventMonitor m " + "WHERE m.contObjectId IN ( "
			+ "SELECT co.id FROM Subscriber s LEFT JOIN s.contObjects co "
			+ " WHERE s.id = :subscriberId)")
	public List<ContEventMonitor> selectBySubscriberId(
			@Param("subscriberId") Long subscriberId);

}
