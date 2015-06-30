package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;

public interface ContEventMonitorRepository extends
		JpaRepository<ContEventMonitor, Long> {

	public List<ContEventMonitor> findByContObjectId(Long contObjectId);
}
