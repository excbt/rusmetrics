package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;

public interface SubscrContEventNotificationRepository extends
		PagingAndSortingRepository<SubscrContEventNotification, Long> {

	@Query("SELECT cen FROM SubscrContEventNotification cen LEFT JOIN FETCH cen.contEvent ce LEFT JOIN FETCH ce.contEventType cet "
			+ "WHERE cen.subscriber.id = :subscriberId "
			+ "ORDER BY cen.contEventTime DESC")
	public List<SubscrContEventNotification> selectContEventNotification(
			@Param("subscriberId") long subscriberId, Pageable pageable);

}
