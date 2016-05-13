package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;

public interface SubscrObjectTreeTemplateRepository extends CrudRepository<SubscrObjectTreeTemplate, Long> {

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT t FROM SubscrObjectTreeTemplate t "
			+ " WHERE t.isCommon = true OR t.rmaSubscriberId = :rmaSubscriberId " + " ORDER BY t.templateOrder")
	public List<SubscrObjectTreeTemplate> selectRmaSubscriberTemplates(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT t FROM SubscrObjectTreeTemplate t " + " WHERE t.isCommon = true OR t.subscriberId = :subscriberId "
			+ " ORDER BY t.templateOrder")
	public List<SubscrObjectTreeTemplate> selectSubscriberTemplates(@Param("subscriberId") Long subscriberId);

}
