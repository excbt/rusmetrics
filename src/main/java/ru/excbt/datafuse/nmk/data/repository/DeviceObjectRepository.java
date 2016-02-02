package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;

/**
 * Repository для DeviceObject
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
public interface DeviceObjectRepository extends CrudRepository<DeviceObject, Long> {

	@Query("SELECT do FROM DeviceObject do LEFT JOIN do.contObject co " + " WHERE co.id = :contObjectId")
	public List<DeviceObject> selectDeviceObjectsByContObjectId(@Param("contObjectId") Long contObjectId);

}
