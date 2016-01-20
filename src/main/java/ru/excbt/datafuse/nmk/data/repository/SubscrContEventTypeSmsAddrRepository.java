package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;

public interface SubscrContEventTypeSmsAddrRepository extends CrudRepository<SubscrContEventTypeSmsAddr, Long> {

	List<SubscrContEventTypeSmsAddr> findBySubscrContEventTypeSmsId(Long subscrContEventTypeSmsId);
}
