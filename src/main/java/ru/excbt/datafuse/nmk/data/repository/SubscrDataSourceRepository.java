package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;

/**
 * Repository для SubscrDataSource
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.10.2015
 *
 */
public interface SubscrDataSourceRepository extends CrudRepository<SubscrDataSource, Long> {

	public List<SubscrDataSource> findBySubscriberId(Long subscriberId);

}
