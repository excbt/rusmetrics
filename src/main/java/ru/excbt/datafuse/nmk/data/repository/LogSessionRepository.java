package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LogSession;

public interface LogSessionRepository extends JpaRepository<LogSession, Long> {

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query("SELECT s FROM LogSession s WHERE (s.sessionDate BETWEEN :startDate AND :endDate) AND s.deleted = 0 "
			+ " AND (s.dataSourceId IN (:dataSourceIds) )")
	public List<LogSession> selectLogSessions(@Param("dataSourceIds") List<Long> dataSourceIds,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param contObjectIds
	 * @return
	 */
	@Query("SELECT s FROM LogSession s WHERE (s.sessionDate BETWEEN :startDate AND :endDate) "
			+ " AND s.deleted = 0 "
			+ " AND (s.deviceObjectId IN "
			+ "  (SELECT d.id FROM DeviceObject d WHERE d.contObject.id IN (:contObjectIds)) "
			+ " ) AND s.dataSourceId IN (:dataSourceIds) ")
	public List<LogSession> selectLogSessions(@Param("dataSourceIds") List<Long> dataSourceIds,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("contObjectIds") List<Long> contObjectIds);

}
