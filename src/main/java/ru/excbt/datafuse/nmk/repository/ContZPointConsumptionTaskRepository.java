package ru.excbt.datafuse.nmk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumptionTask;

import java.util.List;
import java.util.UUID;

public interface ContZPointConsumptionTaskRepository extends JpaRepository<ContZPointConsumptionTask, Long> {

    List<ContZPointConsumptionTask> findByTaskUUID(UUID uuid);

}
