package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;

public interface SubscrContObjectRepository extends CrudRepository<SubscrContObject, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<SubscrContObject> findByContObjectId(Long contObjectId);

	@Query("SELECT DISTINCT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId IN "
			+ " (SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId IS NOT NULL) "
			+ " AND :subscrDate  >= sco.subscrBeginDate AND sco.subscrEndDate IS NULL ")
	public List<Long> selectRmaSubscrContObjectIds(@Param("subscrDate") Date subscrDate);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId")
	public List<ContObject> selectContObjects(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId")
	public List<Long> selectContObjectIds(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.contObjectId = :contObjectId")
	public List<Long> selectContObjectId(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") long contObjectId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT zp FROM ContZPoint zp WHERE zp.contObjectId IN "
			+ " (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId )")
	public List<ContZPoint> selectContZPoints(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT do FROM DeviceObject do LEFT JOIN do.contObject dco "
			+ " WHERE dco.id IN (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId = :subscriberId)")
	public List<DeviceObject> selectDeviceObjects(@Param("subscriberId") Long subscriberId);
}
