package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;

public interface SubscrActionGroupRepository extends CrudRepository<SubscrActionGroup, Long> {

	public List<SubscrActionGroup> findBySubscriberId(long subscriberId);
	
}
