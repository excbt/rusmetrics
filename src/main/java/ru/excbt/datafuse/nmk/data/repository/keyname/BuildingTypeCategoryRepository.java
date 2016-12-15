/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.BuildingTypeCategory;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public interface BuildingTypeCategoryRepository extends JpaRepository<BuildingTypeCategory, String> {

}
