package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.ReportPeriod;

/**
 * Repository для ReportPeriod
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface ReportPeriodRepository extends CrudRepository<ReportPeriod, String> {

	public List<ReportPeriod> findByKeynameIgnoreCase(String keyname);

	@Query("SELECT rp FROM ReportPeriod rp ORDER BY rp.reportPeriodOrder")
	public List<ReportPeriod> selectReportPeriods();
}
