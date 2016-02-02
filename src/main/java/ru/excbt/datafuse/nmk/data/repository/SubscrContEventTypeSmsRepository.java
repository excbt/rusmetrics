package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;

/**
 * Repository для SubscrContEventTypeSms
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
public interface SubscrContEventTypeSmsRepository extends CrudRepository<SubscrContEventTypeSms, Long> {

	List<SubscrContEventTypeSms> findBySubscriberId(Long subscriberId);

}
