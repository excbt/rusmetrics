package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

public interface ContServiceTypeRepository extends JpaRepository<ContServiceType, String> {

	@Query("SELECT cst FROM ContServiceType cst ORDER BY cst.serviceOrder ASC, cst.name ASC")
	public List<ContServiceType> selectAll();
}
