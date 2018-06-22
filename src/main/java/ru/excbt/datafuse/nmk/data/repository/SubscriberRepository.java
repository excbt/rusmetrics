package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

import java.util.List;

/**
 * Repository для Subscriber
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.03.2015
 *
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, QuerydslPredicateExecutor<Subscriber> {

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
     * @param rmaSubscriberId
     * @return
     */
	@Query("SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId")
	public List<Long> findIdsByRmaSubscriberId(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT s FROM Subscriber s WHERE s.rmaSubscriberId = :rmaSubscriberId ORDER BY s.subscriberName")
	public List<Subscriber> findByRmaSubscriberId(@Param("rmaSubscriberId") Long rmaSubscriberId);


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
	List<Subscriber> finaAllRma();

    /**
     *
     * @param parentSubscriberId
     * @return
     */
	@Query("SELECT s FROM Subscriber s WHERE s.parentSubscriberId = :parentSubscriberId and s.isChild = true ORDER BY s.id ")
	public List<Subscriber> selectChildSubscribers(@Param("parentSubscriberId") Long parentSubscriberId);

    @Query("SELECT s FROM Subscriber s WHERE s.parentSubscriberId = :parentSubscriberId ORDER BY s.id ")
    List<Subscriber> selectSubscribers(@Param("parentSubscriberId") Long parentSubscriberId);

    @Query("SELECT s FROM Subscriber s WHERE s.parentSubscriberId = :parentSubscriberId")
    Page<Subscriber> selectSubscribers(@Param("parentSubscriberId") Long parentSubscriberId, Pageable pageable);
}
