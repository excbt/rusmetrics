package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;

/**
 * Repository для SystemUser
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
public interface SystemUserRepository extends CrudRepository<SystemUser, Long> {

	List<SystemUser> findByUserName(String userName);

    @Query("SELECT u.id FROM SystemUser u INNER JOIN u.subscriber s WHERE u.subscriber.id = :subscriberId OR (s.rmaSubscriberId = :subscriberId)")
    List<Long> findUserIdsBySubscriberOrRmaId(@Param("subscriberId") Long subscriberId);

    Optional<SystemUser> findOneByUserName(String userName);


}
