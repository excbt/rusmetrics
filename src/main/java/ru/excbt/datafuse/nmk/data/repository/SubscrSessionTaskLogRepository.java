package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTaskLog;

public interface SubscrSessionTaskLogRepository extends JpaRepository<SubscrSessionTaskLog, Long> {

	@Query("SELECT s FROM LogSession s "
			+ " WHERE s.id IN (SELECT t.logSessionId FROM SubscrSessionTaskLog t "
			+ " WHERE t.subscrSessionTaskId = :subscrSessionTaskId AND (t.logSessionId <> 0 AND t.logSessionId IS NOT NULL))")
	public List<LogSession> selectTaskLogSessions(@Param("subscrSessionTaskId") long subscrSessionTaskId);
}
