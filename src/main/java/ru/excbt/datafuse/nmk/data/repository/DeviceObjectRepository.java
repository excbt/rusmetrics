package ru.excbt.datafuse.nmk.data.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
public interface DeviceObjectRepository extends JpaRepository<DeviceObject, Long> {

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT do FROM DeviceObject do LEFT JOIN do.contObject co "
			+ " WHERE co.id = :contObjectId ORDER BY do.number NULLS LAST")
	public List<DeviceObject> selectDeviceObjectsByContObjectId(@Param("contObjectId") Long contObjectId);

	/**
	 *
	 * @param ids
	 * @return
	 */
	@Query("SELECT do FROM DeviceObject do WHERE do.id IN (:ids) ORDER BY do.id")
	public List<DeviceObject> selectDeviceObjectsByIds(@Param("ids") Collection<Long> ids);

}
