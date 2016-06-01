package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LogSession;

public interface LogSessionRepository extends JpaRepository<LogSession, Long> {

	@Query("SELECT s FROM LogSession s WHERE s.sessionDate BETWEEN :startDate AND :endDate")
	public List<LogSession> selectLogSessions(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
