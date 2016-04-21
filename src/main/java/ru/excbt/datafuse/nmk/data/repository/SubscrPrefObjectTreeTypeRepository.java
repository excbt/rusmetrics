package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrPrefObjectTreeType;

public interface SubscrPrefObjectTreeTypeRepository extends CrudRepository<SubscrPrefObjectTreeType, Long> {

	public List<SubscrPrefObjectTreeType> findBySubscrPrefKeyname(String subscrPrefKeyname);

}
