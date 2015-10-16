package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public interface SubscriberRepository extends CrudRepository<Subscriber, Long> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.organization.id = :organizationId")
	public List<Subscriber> selectByOrganizationId(@Param("organizationId") Long organizationId);

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Query("SELECT s FROM SubscrUser su INNER JOIN su.subscriber s WHERE su.id = :subscrUserId")
	public List<Subscriber> selectByUserId(@Param("subscrUserId") Long subscrUserId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT r.organization FROM SubscrRso r WHERE r.subscriberId = :subscriberId")
	public List<Organization> selectRsoOrganizations(@Param("subscriberId") Long subscriberId);

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

	public List<Subscriber> findByRmaSubscriberId(Long subscriberId);

}
