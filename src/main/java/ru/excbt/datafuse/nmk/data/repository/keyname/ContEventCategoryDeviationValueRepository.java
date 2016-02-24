package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategoryDeviationValue;

public interface ContEventCategoryDeviationValueRepository
		extends CrudRepository<ContEventCategoryDeviationValue, String> {

	@Query("SELECT dv FROM ContEventCategoryDeviationValue dv WHERE dv.contEventCategoryDeviationKeyname = :deviationKeyname ORDER BY dv.valueOrder")
	public List<ContEventCategoryDeviationValue> selectContEventCategoryDeviationValue(
			@Param("deviationKeyname") String deviationKeyname);

}
