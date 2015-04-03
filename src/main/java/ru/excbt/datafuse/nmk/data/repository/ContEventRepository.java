package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;

public interface ContEventRepository extends PagingAndSortingRepository<ContEvent, Long> {

	public List<ContEvent> findByContObjectId(Long contObjectId);
	
//	@Query("SELECT ce FROM ContEvent ce INNER JOIN ce.contObject c  "
//			+ "WHERE c IN (SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :subscriberId)")	
	@Transactional(readOnly = true)	
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObject IN (SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")	
	public List<ContEvent> selectBySubscriberId(@Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)	
	@Query("SELECT ce FROM ContEvent ce "
			+ "WHERE ce.contObject IN (SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :subscriberId) "
			+ "ORDER BY ce.eventTime DESC")	
	public Page<ContEvent> selectBySubscriberId(@Param("subscriberId") long subscriberId, Pageable pageable);
	
}
