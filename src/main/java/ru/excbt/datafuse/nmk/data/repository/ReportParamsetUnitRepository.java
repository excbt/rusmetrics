package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;

public interface ReportParamsetUnitRepository extends
		CrudRepository<ReportParamsetUnit, Long> {

	@Query("SELECT co FROM ContObject co WHERE co.id IN "
			+ "( SELECT u.objectId FROM ReportParamsetUnit u LEFT JOIN u.reportParamset rp "
			+ "WHERE rp.id = :reportParamsetId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectContObjects(
			@Param("reportParamsetId") long reportParamsetId);

	@Query("SELECT co FROM Subscriber s LEFT JOIN s.contObjects co "
			+ "WHERE s.id = :subscriberId AND co.id NOT IN "
			+ "( SELECT u.objectId FROM ReportParamsetUnit u LEFT JOIN u.reportParamset rp "
			+ "WHERE rp.id = :reportParamsetId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectAvailableContObjects(
			@Param("reportParamsetId") long reportParamsetId,
			@Param("subscriberId") long subscriberId);

	@Query("SELECT u.id FROM ReportParamsetUnit u "
			+ "WHERE u.reportParamset.id = :reportParamsetId AND u.objectId = :objectId ")
	public List<Long> selectUnitIds(
			@Param("reportParamsetId") long reportParamsetId,
			@Param("objectId") long objectId);

	@Query("SELECT u.id FROM ReportParamsetUnit u "
			+ "WHERE u.reportParamset.id = :reportParamsetId ")
	public List<Long> selectUnitIds(
			@Param("reportParamsetId") long reportParamsetId);

	@Query("SELECT u.objectId FROM ReportParamsetUnit u "
			+ "WHERE u.reportParamset.id = :reportParamsetId ")
	public List<Long> selectObjectIds(
			@Param("reportParamsetId") long reportParamsetId);

	@Modifying
	@Query("UPDATE ReportParamsetUnit pu SET deleted = 1 WHERE pu.reportParamset.id = :reportParamsetId ")
	public void softDeleteByReportParamset(
			@Param("reportParamsetId") long reportParamsetId);

	/**
	 * 
	 */
}
