package ru.excbt.datafuse.nmk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;

@Repository
public interface ContZPointConsumptionRepository extends JpaRepository<ContZPointConsumption, Long> {

}
