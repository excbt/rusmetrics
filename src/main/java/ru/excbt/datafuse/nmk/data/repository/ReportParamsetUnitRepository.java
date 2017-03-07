package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;

/**
 * Repository для ReportParamsetUnit
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.04.2015
 *
 */
public interface ReportParamsetUnitRepository extends JpaRepository<ReportParamsetUnit, Long> {

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Query("SELECT co FROM ContObject co WHERE co.id IN "
			+ "( SELECT u.objectId FROM ReportParamsetUnit u LEFT JOIN u.reportParamset rp "
			+ "WHERE rp.id = :reportParamsetId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectContObjects(@Param("reportParamsetId") long reportParamsetId);

	/**
	 *
	 * @param reportParamsetId
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco "
			+ "WHERE sco.subscriberId = :subscriberId AND sco.contObjectId NOT IN "
			+ "( SELECT u.objectId FROM ReportParamsetUnit u LEFT JOIN u.reportParamset rp "
			+ "WHERE rp.id = :reportParamsetId ) AND sco.subscrEndDate IS NULL "
			+ "ORDER BY sco.contObject.name, sco.contObject.id")
	public List<ContObject> selectAvailableContObjects(@Param("reportParamsetId") long reportParamsetId,
			@Param("subscriberId") long subscriberId);

	/**
	 *
	 * @param reportParamsetId
	 * @param objectId
	 * @return
	 */
	@Query("SELECT u.id FROM ReportParamsetUnit u "
			+ "WHERE u.reportParamset.id = :reportParamsetId AND u.objectId = :objectId ")
	public List<Long> selectUnitIds(@Param("reportParamsetId") long reportParamsetId, @Param("objectId") long objectId);

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Query("SELECT u.id FROM ReportParamsetUnit u " + "WHERE u.reportParamset.id = :reportParamsetId ")
	public List<Long> selectUnitIds(@Param("reportParamsetId") long reportParamsetId);

	/**
	 *
	 * @param reportParamsetId
	 * @return
	 */
	@Query("SELECT u.objectId FROM ReportParamsetUnit u " + "WHERE u.reportParamset.id = :reportParamsetId ")
	public List<Long> selectObjectIds(@Param("reportParamsetId") long reportParamsetId);

	/**
	 *
	 * @param reportParamsetId
	 */
	@Modifying
	@Query("UPDATE ReportParamsetUnit pu SET deleted = 1 WHERE pu.reportParamset.id = :reportParamsetId ")
	public void softDeleteByReportParamset(@Param("reportParamsetId") long reportParamsetId);

	/**
	 *
	 */
}
