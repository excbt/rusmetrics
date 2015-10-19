package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrContObject;

public interface SubscrContObjectRepository extends CrudRepository<SubscrContObject, Long> {

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	public List<SubscrContObject> findByContObjectId(Long contObjectId);

	@Query("SELECT DISTINCT sco.contObjectId FROM SubscrContObject sco WHERE sco.subscriberId IN "
			+ " (SELECT s.id FROM Subscriber s WHERE s.rmaSubscriberId IS NOT NULL) "
			+ " AND :subscrDate  >= sco.subscrBeginDate AND sco.subscrEndDate IS NULL ")
	public List<Long> selectRmaSubscrContObjectIds(@Param("subscrDate") Date subscrDate);

}
