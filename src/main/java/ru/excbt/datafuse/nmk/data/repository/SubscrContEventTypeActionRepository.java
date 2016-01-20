package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;

public interface SubscrContEventTypeActionRepository extends CrudRepository<SubscrContEventTypeAction, Long> {

	@Query("SELECT a FROM SubscrContEventTypeAction a WHERE a.subscriberId = :subscriberId AND a.contEventTypeId = :contEventTypeId")
	public List<SubscrContEventTypeAction> selectSubscrContEventActions(@Param("subscriberId") Long subscriberId,
			@Param("contEventTypeId") Long contEventTypeId);
}
