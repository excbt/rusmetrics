package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;

public interface ReportParamsetUnitRepository extends CrudRepository<ReportParamsetUnit, Long> {

	@Query("SELECT co FROM ContObject co WHERE co.id IN "
			+ "( SELECT u.objectId FROM ReportParamsetUnit u LEFT JOIN u.reportParamset rp "
			+ "WHERE rp.id = :reportParamsetId ) ")
	public List<ContObject> selectContObjectUnits(@Param("reportParamsetId") long reportParamsetId);
}
