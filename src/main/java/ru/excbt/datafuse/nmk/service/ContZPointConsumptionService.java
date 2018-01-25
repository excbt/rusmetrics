package ru.excbt.datafuse.nmk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.domain.QContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;

@Service
public class ContZPointConsumptionService {

    private final QueryDSLService queryDSLService;

    private final ContZPointConsumptionRepository consumptionRepository;

    @Autowired
    public ContZPointConsumptionService(QueryDSLService queryDSLService, ContZPointConsumptionRepository consumptionRepository) {
        this.queryDSLService = queryDSLService;
        this.consumptionRepository = consumptionRepository;
    }


    @Transactional
    public void getConsumption() {

        QContZPointConsumption qContZPointConsumption = QContZPointConsumption.contZPointConsumption;

        //consumptionRepository.f
    }


}
