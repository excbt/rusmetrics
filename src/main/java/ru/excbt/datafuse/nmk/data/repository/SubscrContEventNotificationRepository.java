package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;

public interface SubscrContEventNotificationRepository extends
		PagingAndSortingRepository<SubscrContEventNotification, Long>,
		JpaSpecificationExecutor<SubscrContEventNotification> {

}
