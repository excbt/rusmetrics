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

/**
 * Repository для SubscrContObject
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
public interface SubscrContObjectRepository extends CrudRepository<SubscrContObject, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<SubscrContObject> findByContObjectId(Long contObjectId);

	@Query("SELECT DISTINCT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId IN "
			+ " (SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId = :subscriberId AND s.deleted = 0) "
			+ " AND :subscrDate  >= sco.subscrBeginDate AND sco.subscrEndDate IS NULL AND sco.deleted = 0 ")
	public List<Long> selectRmaSubscrContObjectIds(@Param("subscriberId") Long subscriberId,
			@Param("subscrDate") Date subscrDate);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL "
			+ " AND sco.deleted = 0" + " ORDER BY sco.contObject.fullAddress, sco.contObject.id")
	public List<ContObject> selectContObjects(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL "
			+ " AND sco.deleted = 0 AND sco.contObjectId NOT IN (:idList)"
			+ " ORDER BY sco.contObject.fullAddress, sco.contObject.id")
	public List<ContObject> selectContObjectsExcludingIds(@Param("subscriberId") Long subscriberId,
			@Param("idList") List<Long> idList);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL "
			+ " AND sco.deleted = 0")
	public List<Long> selectContObjectIds(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.contObjectId = :contObjectId "
			+ " AND sco.deleted = 0 AND sco.subscrEndDate IS NULL")
	public List<Long> selectContObjectId(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") long contObjectId);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT sco FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.contObjectId = :contObjectId "
			+ " AND sco.deleted = 0 AND sco.subscrEndDate IS NULL ")
	public List<SubscrContObject> selectActualSubscrContObjects(@Param("subscriberId") Long subscriberId,
			@Param("contObjectId") long contObjectId);

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT sco FROM SubscrContObject sco " + " WHERE sco.subscriberId = :subscriberId ")
	public List<SubscrContObject> selectSubscrContObjects(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT zp FROM ContZPoint zp WHERE zp.contObjectId IN "
			+ " (SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL)")
	public List<ContZPoint> selectContZPoints(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT zp.id, zp.contObjectId, zp.customServiceName, zp.contServiceTypeKeyname, st.caption "
			+ " FROM ContZPoint zp INNER JOIN zp.contServiceType st WHERE zp.contObjectId IN "
			+ " (SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL) AND zp.deleted = 0")
	public List<Object[]> selectContZPointShortInfo(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT zp.id " + " FROM ContZPoint zp INNER JOIN zp.contServiceType st WHERE zp.contObjectId IN "
			+ " (SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.deleted = 0 AND sco.subscrEndDate IS NULL) AND zp.deleted = 0")
	public List<Long> selectContZPointIds(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT do FROM DeviceObject do LEFT JOIN do.contObject dco "
			+ " WHERE dco.id IN (SELECT sco.contObjectId FROM SubscrContObject sco "
			+ " WHERE sco.subscriberId = :subscriberId AND sco.subscrEndDate IS NULL AND sco.deleted = 0)"
			+ " ORDER BY do.contObject.fullAddress, do.contObject.id ")
	public List<DeviceObject> selectDeviceObjects(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public List<SubscrContObject> findBySubscriberId(Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT rco.contObject FROM SubscrContObject rco WHERE rco.subscriberId = :rmaSubscriberId AND rco.contObjectId NOT IN "
			+ " (SELECT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId=:subscriberId AND sco.subscrEndDate IS NULL AND sco.deleted = 0) "
			+ " ORDER BY rco.contObject.fullAddress, rco.contObject.id ")
	public List<ContObject> selectAvailableContObjects(@Param("subscriberId") Long subscriberId,
			@Param("rmaSubscriberId") Long rmaSubscriberId);

}
