package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;

/**
 * Repository для ContGroup
 *
 * @author STA.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */
public interface SubscrContGroupRepository extends CrudRepository<SubscrContGroup, Long> {

	public List<SubscrContGroup> findBySubscriberId(long subscriberId);

}
