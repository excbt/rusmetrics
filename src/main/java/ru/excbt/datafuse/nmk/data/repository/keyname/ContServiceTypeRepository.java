package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

/**
 * Repository для ContServiceType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.04.2015
 *
 */
public interface ContServiceTypeRepository extends JpaRepository<ContServiceType, String> {

	@Query("SELECT cst FROM ContServiceType cst ORDER BY cst.serviceOrder ASC, cst.name ASC")
	public List<ContServiceType> selectAll();
}
