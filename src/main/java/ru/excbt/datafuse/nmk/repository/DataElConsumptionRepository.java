package ru.excbt.datafuse.nmk.repository;

import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.QContServiceDataElCons;
import ru.excbt.datafuse.nmk.repository.support.ExConsumptionRepositoryRI;

@Repository
public interface DataElConsumptionRepository extends ExConsumptionRepositoryRI<ContServiceDataElCons, QContServiceDataElCons, Long> {



}
