package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;

public interface SubscrServiceAccessRepository extends CrudRepository<SubscrServiceAccess, Long> {

	List<SubscrServiceAccess> findBySubscriberId(Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Query(value = "SELECT sa FROM SubscrServiceAccess sa WHERE sa.subscriberId = :subscriberId "
			+ " AND :accessDate >= accessStartDate and (accessEndDate IS NULL OR :accessDate <= accessEndDate)")
	List<SubscrServiceAccess> selectSubscriberAccessByDate(@Param("subscriberId") Long subscriberId,
			@Param("accessDate") Date accessDate);
}
