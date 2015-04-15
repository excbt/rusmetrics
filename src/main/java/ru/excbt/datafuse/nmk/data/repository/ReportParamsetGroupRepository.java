package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportParamsetGroup;

public interface ReportParamsetGroupRepository extends CrudRepository<ReportParamsetGroup, Long> {

//	@Query("SELECT rp FROM ReportParamset rp LEFT JOIN FETCH rp.reportTemplate rt "
//			+ "WHERE rp.subscriber is null AND rt.reportTypeKey = :reportType AND "
//			+ "rp._active = :isActive "
//			+ "ORDER BY rp.activeStartDate, rp.name")
//	public List<ContObject> selectContObjectGroup(@Param("reportParamsetId") long reportParamsetId);
}
