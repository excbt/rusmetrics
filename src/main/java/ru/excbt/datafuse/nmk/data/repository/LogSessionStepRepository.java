package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LogSessionStep;

public interface LogSessionStepRepository extends JpaRepository<LogSessionStep, Long> {

	@Query("SELECT s FROM LogSessionStep s WHERE s.sessionId = :sessionId ORDER BY s.stepDate, s.createdDate")
	public List<LogSessionStep> selectSessionSteps(@Param("sessionId") long sessionId);

}
