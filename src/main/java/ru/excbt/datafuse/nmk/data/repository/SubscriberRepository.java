package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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
	public List<Subscriber> findByRmaSubscriberId(Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT s.id FROM Subscriber s WHERE s.id = :subscriberId")
	public List<Long> checkSubscriberId(@Param("subscriberId") Long subscriberId);

}
