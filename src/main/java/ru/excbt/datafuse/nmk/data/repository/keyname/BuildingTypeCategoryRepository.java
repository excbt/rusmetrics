/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.BuildingTypeCategory;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
public interface BuildingTypeCategoryRepository extends JpaRepository<BuildingTypeCategory, String> {

	/**
	 * 
	 * @return
	 */
	@Query("SELECT c FROM BuildingTypeCategory c ORDER BY c.keyname, c.buildingType")
	public List<BuildingTypeCategory> selectAllBuildingTypes();

}
