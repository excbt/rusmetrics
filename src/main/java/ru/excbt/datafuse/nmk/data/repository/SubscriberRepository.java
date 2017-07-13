package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

/**
 * Repository для Subscriber
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.03.2015
 *
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

	/**
	 *
	 * @param id
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.organizationId = :organizationId")
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
	public List<Subscriber> findByRmaSubscriberId(Long rmaSubscriberId);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId")
	public List<Long> selectByRmaSubscriberIds(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId ORDER BY s.subscriberName")
	public List<Subscriber> selectByRmaSubscriberId(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT s.id FROM Subscriber s WHERE s.id = :subscriberId")
	public List<Long> checkSubscriberId(@Param("subscriberId") Long subscriberId);

	/**
	 *
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.isRma = true")
	public List<Subscriber> selectRmaList();

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.parentSubscriberId = :parentSubscriberId and s.isChild = true ORDER BY s.id ")
	public List<Subscriber> selectChildSubscribers(@Param("parentSubscriberId") Long parentSubscriberId);

}
