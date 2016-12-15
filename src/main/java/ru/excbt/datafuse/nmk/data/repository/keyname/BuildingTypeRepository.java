/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.BuildingType;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
public interface BuildingTypeRepository extends JpaRepository<BuildingType, Long> {

	@Query("SELECT b FROM BuildingType b ORDER BY b.orderIdx")
	public List<BuildingType> selectAll();
}
