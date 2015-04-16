package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public interface ReportParamsetRepository extends CrudRepository<ReportParamset, Long> {

	public List<ReportParamset> findByReportTemplateId(long reportTemplateId);

	@Query("SELECT rp FROM ReportParamset rp "
			+ "WHERE rp.reportTemplate.id = :reportTemplateId AND "
			+ "rp._active = :isActive "
			+ "ORDER BY rp.activeStartDate, rp.name")	
	public List<ReportParamset> selectReportParamset(Long reportTemplateId, boolean isActive);

	
	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ "WHERE rp.subscriber is null AND rt.reportTypeKey = :reportType AND "
			+ "rp._active = :isActive "
			+ "ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectCommonReportParamset(
			@Param("reportType") ReportTypeKey reportType,
			@Param("isActive") boolean isActive);	

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ "WHERE rp.subscriber.id = :subscriberId AND rt.reportTypeKey = :reportType AND "
			+ "rp._active = :isActive "
			+ "ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectSubscriberReportParamset(
			@Param("subscriberId") long subscriberId,
			@Param("reportType") ReportTypeKey reportType,
			@Param("isActive") boolean isActive);	

	/**
	 * 
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt.id FROM ReportParamset rt WHERE rt.subscriber IS NULL ")
	public List<Long> selectCommonParamsetIds();	
	
}
