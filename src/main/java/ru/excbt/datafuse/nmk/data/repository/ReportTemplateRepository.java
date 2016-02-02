package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

/**
 * Repository для ReportTemplate
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface ReportTemplateRepository extends CrudRepository<ReportTemplate, Long> {

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt " + "WHERE rt.reportTypeKeyname = :reportTypeKeyname AND "
			+ "rt._active = :isActive " + "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectActiveTemplates(@Param("reportTypeKeyname") String reportTypeKeyname,
			@Param("isActive") boolean isActive);

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber IS NULL AND "
			+ "rt.reportTypeKeyname = :reportTypeKeyname AND " + "rt._active = :isActive "
			+ "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectCommonTemplates(@Param("reportTypeKeyname") String reportTypeKeyname,
			@Param("isActive") boolean isActive);

	/**
	 * 
	 * @param subscriberId
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt FROM ReportTemplate rt WHERE rt.subscriber.id = :subscriberId AND "
			+ "rt.reportTypeKeyname = :reportTypeKeyname AND " + "rt._active = :isActive "
			+ "ORDER BY rt.activeStartDate, rt.name")
	public List<ReportTemplate> selectSubscriberTemplates(@Param("reportTypeKeyname") String reportTypeKeyname,
			@Param("isActive") boolean isActive, @Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt.id FROM ReportTemplate rt WHERE rt.subscriber IS NULL ")
	public List<Long> selectCommonTemplateIds();

}
