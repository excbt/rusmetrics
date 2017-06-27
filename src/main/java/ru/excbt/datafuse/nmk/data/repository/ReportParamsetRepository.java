package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.repository.support.SubscriberRI;

/**
 * Repository для ReportParamset
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface ReportParamsetRepository extends JpaRepository<ReportParamset, Long>, SubscriberRI<ReportParamset> {

	public List<ReportParamset> findByReportTemplateId(Long reportTemplateId);

	//public List<ReportParamset> findBySubscriberId(Long subscriberId);

	/**
	 *
	 * @param reportTemplateId
	 * @param isActive
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp " + "WHERE rp.reportTemplate.id = :reportTemplateId AND "
			+ "rp._active = :isActive " + "ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectReportParamset(@Param("reportTemplateId") Long reportTemplateId,
			@Param("isActive") boolean isActive);

	/**
	 *
	 * @param reportTemplateId
	 * @param activeDate
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp " + "WHERE rp.reportTemplate.id = :reportTemplateId AND "
			+ "rp.activeStartDate <= :activeDate AND "
			+ " (rp.activeEndDate IS NULL OR rp.activeEndDate >= :activeDate) "
			+ "ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectReportParamset(@Param("reportTemplateId") Long reportTemplateId,
			@Param("activeDate") Date activeDate);

	/**
	 *
	 * @param reportTypeKeyname
	 * @param isActive
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ "WHERE rp.subscriber is null AND rt.reportTypeKeyname = :reportTypeKeyname AND "
			+ "rp._active = :isActive " + "ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectCommonReportParamset(@Param("reportTypeKeyname") String reportTypeKeyname,
			@Param("isActive") boolean isActive);

	/**
	 *
	 * @param reportTypeKeyname
	 * @param isActive
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ "WHERE rp.subscriber.id = :subscriberId AND rt.reportTypeKeyname = :reportTypeKeyname AND "
			+ "rp._active = :isActive AND rp.deleted = 0 " + " ORDER BY rp.activeStartDate, rp.name")
	public List<ReportParamset> selectSubscriberReportParamset(@Param("reportTypeKeyname") String reportTypeKeyname,
			@Param("isActive") boolean isActive, @Param("subscriberId") long subscriberId);

	/**
	 *
	 * @param reportType
	 * @param currentDate
	 * @return
	 */
	@Query("SELECT rt.id FROM ReportParamset rt WHERE rt.subscriber IS NULL ")
	public List<Long> selectCommonParamsetIds();

	/**
	 *
	 * @param reportType
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ " WHERE rp.subscriber.id = :subscriberId AND rp._active = true AND rp.isContextLaunch = true "
			+ " ORDER BY rp.name NULLS LAST, rp.name")
	public List<ReportParamset> selectReportParamsetContextLaunch(@Param("subscriberId") Long subscriberId);

	/**
	 *
	 * @param reportType
	 * @return
	 */
	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
			+ " WHERE rp.subscriber.id = :rmaSubscriberId AND rp._active = true AND rp.isContextLaunchChild = true "
			+ " ORDER BY rp.name NULLS LAST, rp.name")
	public List<ReportParamset> selectRmaReportParamsetContextLaunchChild(
			@Param("rmaSubscriberId") Long rmaSubscriberId);

}
