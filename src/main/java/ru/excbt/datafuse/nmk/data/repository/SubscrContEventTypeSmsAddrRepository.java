package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;

/**
 * Repository для SubscrContEventTypeSmsAddr
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
public interface SubscrContEventTypeSmsAddrRepository extends CrudRepository<SubscrContEventTypeSmsAddr, Long> {

	List<SubscrContEventTypeSmsAddr> findBySubscrContEventTypeSmsId(Long subscrContEventTypeSmsId);
}
