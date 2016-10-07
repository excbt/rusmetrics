/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.DeviceModelType;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.10.2016
 * 
 */
public interface DeviceModelTypeRepository extends CrudRepository<DeviceModelType, Long> {

	@Query("SELECT d FROM DeviceModelType d ORDER BY d.orderIdx")
	public List<DeviceModelType> selectAll();
}
