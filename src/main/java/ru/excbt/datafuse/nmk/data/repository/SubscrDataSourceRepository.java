package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;

public interface SubscrDataSourceRepository extends CrudRepository<SubscrDataSource, Long> {

	public List<SubscrDataSource> findBySubscriberId(Long subscriberId);

}
