package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.ContEventDeviation;

public interface ContEventDeviationRepository extends JpaRepository<ContEventDeviation, String> {

	@Query("SELECT d FROM ContEventDeviation d ORDER BY d.deviationOrder")
	public List<ContEventDeviation> selectContEventDeviation();
}
