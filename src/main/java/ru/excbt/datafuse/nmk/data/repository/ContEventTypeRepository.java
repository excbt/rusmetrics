package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEventType;

/**
 * Repository для ContEventType
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
public interface ContEventTypeRepository extends CrudRepository<ContEventType, Long> {

	@Query("SELECT t FROM ContEventType t " + " WHERE t.isBaseEvent = :isBaseEvent ORDER BY t.name")
	public List<ContEventType> selectBaseEventTypes(@Param("isBaseEvent") Boolean isBaseEvent);

	@Query("SELECT t FROM ContEventType t " + " WHERE t.id IN (:contEventTypeIds) ")
	public List<ContEventType> selectContEventTypes(@Param("contEventTypeIds") List<Long> contEventTypeIds);

	@Query("SELECT s FROM ContEventType s WHERE s.isSmsNotification = true")
	public List<ContEventType> selectBySmsNotification();

}
