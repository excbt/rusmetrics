package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;

/**
 * Repository для SubscrActionGroup
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
public interface SubscrActionGroupRepository extends CrudRepository<SubscrActionGroup, Long> {

	public List<SubscrActionGroup> findBySubscriberId(long subscriberId);

}
