package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventType;

public interface ContEventTypeRepository extends
		CrudRepository<ContEventType, Long> {

	@Query("SELECT t FROM ContEventType t "
			+ " WHERE t.isBaseEvent = :isBaseEvent ORDER BY t.name")
	public List<ContEventType> selectBaseEventTypes(
			@Param("isBaseEvent") Boolean isBaseEvent);

	@Query("SELECT t FROM ContEventType t "
			+ " WHERE t.id IN (:contEventTypes) ")
	public List<ContEventType> selectContEventTypes(
			@Param("contEventTypes") List<Long> contEventTypes);
}
