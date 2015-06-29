package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;

public interface ContEventMonitorRepository extends
		JpaRepository<ContEventMonitor, Long> {

}
