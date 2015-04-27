package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

public interface ReportTemplateRepository extends
		CrudRepository<ReportTemplate, Long> {

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt "
			+ "WHERE rt.reportTypeKey = :reportType AND " + "rt._active = :isActive "
			+ "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectActiveTemplates(
			@Param("reportType") ReportTypeKey reportType,
			@Param("isActive") boolean isActive);

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber IS NULL AND "
			+ "rt.reportTypeKey = :reportType AND " + "rt._active = :isActive "
			+ "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectCommonTemplates(
			@Param("reportType") ReportTypeKey reportType,
			@Param("isActive") boolean isActive);

	/**
	 * 
	 * @param subscriberId
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber.id = :subscriberId AND "
			+ "rt.reportTypeKey = :reportType AND "
			+ "rt._active = :isActive "
			+ "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectSubscriberTemplates(
			@Param("reportType") ReportTypeKey reportType,
			@Param("isActive") boolean isActive,
			@Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt.id FROM ReportTemplate rt WHERE rt.subscriber IS NULL ")
	public List<Long> selectCommonTemplateIds();

}
