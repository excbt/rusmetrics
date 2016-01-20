package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;

public interface SubscrContEventTypeSmsRepository extends CrudRepository<SubscrContEventTypeSms, Long> {

	List<SubscrContEventTypeSms> findBySubscriberId(Long subscriberId);

}
