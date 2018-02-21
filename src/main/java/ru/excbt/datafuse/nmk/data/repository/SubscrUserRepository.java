package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

/**
 * Repository для SubscrUser
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
public interface SubscrUserRepository extends CrudRepository<SubscrUser, Long> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

	List<SubscrUser> findByUserNameIgnoreCase(String userName);

    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
	Optional<SubscrUser> findOneByUserName(String userName);

    Optional<SubscrUser> findOneByUserNameIgnoreCase(String userName);

	@Query("SELECT u.subscrRoles FROM SubscrUser u WHERE u.id = :subscrUserId ")
	List<SubscrRole> selectSubscrRoles(@Param("subscrUserId") long subscrUserId);

	@Query("SELECT u FROM SubscrUser u WHERE u.subscriberId = :subscriberId ORDER BY u.id ")
	List<SubscrUser> selectBySubscriberId(@Param("subscriberId") Long subscriberId);

	@Query("SELECT u.id FROM SubscrUser u INNER JOIN u.subscriber s WHERE u.subscriber.id = :subscriberId OR (s.rmaSubscriberId = :subscriberId)")
	List<Long> findUserIdsBySubscriberOrRmaId(@Param("subscriberId") Long subscriberId);
}
