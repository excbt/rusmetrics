package ru.excbt.datafuse.nmk.data.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;

public interface SubscrActionUserRepository extends CrudRepository<SubscrActionUser, Long> {

	public List<SubscrActionUser> findBySubscriberId(long subscriberId);
}

