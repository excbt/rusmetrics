package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.repository.support.ContObjectRI;

import java.util.List;

/**
 * Repository для ContZPoint
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
public interface ContZPointRepository extends JpaRepository<ContZPoint, Long>, ContObjectRI<ContZPoint> {

	List<ContZPoint> findByIdAndContObject(long contZpointId, long contObjectId);

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT zp.id FROM ContZPoint zp WHERE zp.contObject.id = :contObjectId AND zp.deleted = 0")
	List<Long> findContZPointIds(@Param("contObjectId") long contObjectId);

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT zp.contServiceTypeKeyname, zp.id FROM ContZPoint zp WHERE zp.contObject.id = :contObjectId AND zp.deleted = 0")
	List<Object[]> selectContZPointServiceTypeIds(@Param("contObjectId") long contObjectId);

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT zp.deviceObject FROM ContZPoint zp WHERE zp.id = :contZpointId ")
	List<DeviceObject> selectDeviceObjects(@Param("contZpointId") long contZpointId);

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	@Query("SELECT zp FROM ContZPoint zp INNER JOIN zp.deviceObject d WHERE d.id = :deviceObjectId ")
	List<ContZPoint> selectContZPointsByDeviceObjectId(@Param("deviceObjectId") long deviceObjectId);

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT do.id FROM ContZPoint zp INNER JOIN zp.deviceObject do WHERE zp.id = :contZpointId ")
	List<Long> selectDeviceObjectIds(@Param("contZpointId") long contZpointId);

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Query("SELECT zp.contObjectId FROM ContZPoint zp WHERE zp.id = :contZpointId ")
	List<Long> selectContObjectByContZPointId(@Param("contZpointId") Long contZpointId);

}
