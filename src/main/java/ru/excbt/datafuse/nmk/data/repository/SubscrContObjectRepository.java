package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContObject;

public interface SubscrContObjectRepository extends CrudRepository<SubscrContObject, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<SubscrContObject> findByContObjectId(Long contObjectId);

}
