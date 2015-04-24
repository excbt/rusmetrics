package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.excbt.datafuse.nmk.data.model.ReportShedule;

@Repository
public interface ReportSheduleRepository extends
		CrudRepository<ReportShedule, Long> {

	@Query("SELECT rs FROM ReportShedule rs "
			+ "LEFT JOIN FETCH rs.reportTemplate LEFT JOIN FETCH rs.reportParamset "
			+ "WHERE rs.subscriber.id = :subscriberId AND "
			+ "rs.sheduleStartDate <= :sheduleDate AND "
			+ "( rs.sheduleEndDate IS NULL OR rs.sheduleEndDate >= :sheduleDate)")
	public List<ReportShedule> selectReportShedule(
			@Param("subscriberId") long subscriberId,
			@Param("sheduleDate") Date sheduleDate);

	
	public List<ReportShedule> findBySubscriberId(Long subscriberId);
}
