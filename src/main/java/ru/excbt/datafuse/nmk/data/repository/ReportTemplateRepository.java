package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

public interface ReportTemplateRepository extends
		CrudRepository<ReportTemplate, Long> {

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber IS NULL AND "
			+ "rt.reportType = :reportType AND rt.activeStartDate <= :currentDate AND "
			+ "(rt.activeEndDate >= :currentDate OR rt.activeEndDate IS NULL)")
	public List<ReportTemplate> selectDefaultTemplates(
			@Param("reportType") ReportTypeKeys reportType,
			@Param("currentDate") Date currentDate);

	/**
	 * 
	 * @param subscriberId
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber.id = :subscriberId AND "
			+ "rt.reportType = :reportType AND rt.activeStartDate <= :currentDate AND "
			+ "(rt.activeEndDate >= :currentDate OR rt.activeEndDate IS NULL)")
	public List<ReportTemplate> selectSubscriberTemplates(
			@Param("subscriberId") long subscriberId,
			@Param("reportType") ReportTypeKeys reportType,
			@Param("currentDate") Date currentDate);

	
	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt.id FROM ReportTemplate rt WHERE rt.subscriber IS NULL ")
	public List<Long> selectDefaultTemplateIds();	
	
}
