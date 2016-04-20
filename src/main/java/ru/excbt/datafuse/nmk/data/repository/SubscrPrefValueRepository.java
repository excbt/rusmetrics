package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;

public interface SubscrPrefValueRepository extends CrudRepository<SubscrPrefValue, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT v FROM SubscrPrefValue v WHERE v.subscriberId = :subscriberId")
	public List<SubscrPrefValue> selectSubscrPrefValue(@Param("subscriberId") Long subscriberId);

}
