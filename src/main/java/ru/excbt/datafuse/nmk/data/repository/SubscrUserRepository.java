package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;

public interface SubscrUserRepository extends CrudRepository<SubscrUser, Long> {

	public List<SubscrUser> findByUserNameIgnoreCase(String userName);
	
}
