package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.repository.support.SubscriberRI;

/**
 * Repository для SubscrActionUser
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.04.2015
 *
 */
public interface SubscrActionUserRepository extends CrudRepository<SubscrActionUser, Long>, SubscriberRI<SubscrActionUser> {

}
