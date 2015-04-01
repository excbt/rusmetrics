package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContEvent;

public interface ContEventRepository extends CrudRepository<ContEvent, Long> {

	public List<ContEvent> findByContObjectId(Long contObjectId);
	
//	@Query("SELECT ce FROM ContEvent ce INNER JOIN ce.contObject c  "
//			+ "WHERE c IN (SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :subscriberId)")	
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObject IN (SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")	
	public List<ContEvent> selectBySubscriberId(@Param("subscriberId") long subscriberId);
	
}
