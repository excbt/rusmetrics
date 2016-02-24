package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategoryDeviation;

public interface ContEventCategoryDeviationRepository extends JpaRepository<ContEventCategoryDeviation, String> {

	@Query("SELECT d FROM ContEventCategoryDeviation d WHERE d.contEventCategoryKeyname = :category ORDER BY d.deviationOrder")
	public List<ContEventCategoryDeviation> selectContEventCategoryDeviation(@Param("category") String category);
}
