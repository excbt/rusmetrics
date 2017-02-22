/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 20.02.2017
 * 
 */
public interface MeterPeriodSettingRepository extends CrudRepository<MeterPeriodSetting, Long> {

	@Query("SELECT s FROM MeterPeriodSetting s WHERE s.subscriberId in (:subscriberIds) AND deleted = 0")
	public List<MeterPeriodSetting> findBySubscriberIds (@Param("subscriberIds") List<Long> subscriberIds);
	
}
