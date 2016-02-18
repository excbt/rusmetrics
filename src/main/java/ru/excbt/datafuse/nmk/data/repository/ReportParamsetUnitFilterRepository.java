package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnitFilter;

/**
 * Repository для ReportParamsetUnitFilter
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.11.2015
 *
 */
public interface ReportParamsetUnitFilterRepository extends JpaRepository<ReportParamsetUnitFilter, Long> {

	/**
	 * 
	 * @param reportParamsetId
	 * @return
	 */
	@Query("SELECT u.objectId FROM ReportParamsetUnitFilter u " + "WHERE u.reportParamset.id = :reportParamsetId ")
	public List<Long> selectObjectIds(@Param("reportParamsetId") long reportParamsetId);

}
