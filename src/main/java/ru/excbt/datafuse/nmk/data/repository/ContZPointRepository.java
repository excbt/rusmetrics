package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

/**
 * Repository для ContZPoint
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
public interface ContZPointRepository extends CrudRepository<ContZPoint, Long> {

	public List<ContZPoint> findByContObjectId(Long contObjectId);

	public List<ContZPoint> findByIdAndContObject(long contZpointId, long contObjectId);

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT zp.id FROM ContZPoint zp WHERE zp.contObject.id = :contObjectId ")
	public List<Long> selectContZPointIds(@Param("contObjectId") long contObjectId);

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT zp.contServiceTypeKeyname, zp.id FROM ContZPoint zp WHERE zp.contObject.id = :contObjectId ")
	public List<Object[]> selectContZPointServiceTypeIds(@Param("contObjectId") long contObjectId);

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT zp.deviceObjects FROM ContZPoint zp WHERE zp.id = :contZpointId ")
	public List<DeviceObject> selectDeviceObjects(@Param("contZpointId") long contZpointId);

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Query("SELECT zp FROM ContZPoint zp INNER JOIN zp.deviceObjects d WHERE d.id = :deviceObjectId ")
	public List<ContZPoint> selectContZPointsByDeviceObjectId(@Param("deviceObjectId") long deviceObjectId);

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT do.id FROM ContZPoint zp INNER JOIN zp.deviceObjects do WHERE zp.id = :contZpointId ")
	public List<Long> selectDeviceObjectIds(@Param("contZpointId") long contZpointId);

	/**
	 * 
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT zp.contObjectId FROM ContZPoint zp WHERE zp.id = :contZpointId ")
	public List<Long> selectContObjectByContZPointId(@Param("contZpointId") Long contZpointId);

}
