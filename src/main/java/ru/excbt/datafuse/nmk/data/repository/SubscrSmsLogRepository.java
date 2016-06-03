package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrSmsLog;

public interface SubscrSmsLogRepository extends JpaRepository<SubscrSmsLog, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT ssl FROM SubscrSmsLog ssl WHERE ssl.subscriberId = :subscriberId "
			+ " AND (ssl.smsDate BETWEEN :startDate AND :endDate) "
			+ " ORDER BY ssl.smsDate DESC")
	public List<SubscrSmsLog> selectBySubscriber(@Param("subscriberId") Long subscriberId,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
