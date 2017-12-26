package ru.excbt.datafuse.nmk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ConsumptionService {

    private final ContServiceDataHWaterRepository dataHWaterRepository;

    private final ContServiceDataImpulseRepository dataImpulseRepository;

    private final ContServiceDataElConsRepository dataElConsRepository;

    @Autowired
    public ConsumptionService(ContServiceDataHWaterRepository dataHWaterRepository, ContServiceDataImpulseRepository dataImpulseRepository, ContServiceDataElConsRepository dataElConsRepository) {
        this.dataHWaterRepository = dataHWaterRepository;
        this.dataImpulseRepository = dataImpulseRepository;
        this.dataElConsRepository = dataElConsRepository;
    }


    @Transactional(readOnly = true)
    public List<ZPointConsumptionVM> findConsumption(ContZPoint contZPoint, LocalDateTimePeriod localDateTimePeriod) {

        Objects.requireNonNull(contZPoint);
        Objects.requireNonNull(contZPoint.getId());
        Objects.requireNonNull(contZPoint.getContServiceTypeKeyname());

        ContServiceTypeKey typeKey = ContServiceTypeKey.valueOf(contZPoint.getContServiceTypeKeyname());

        List<ZPointConsumptionVM> resultConsList;

        if (typeKey == null) {
            return Collections.emptyList();
        }

         switch (typeKey) {
            case CW:
            case HW:
            case HEAT:
                resultConsList = findHWaterCons(contZPoint, localDateTimePeriod);
                break;
            default: resultConsList = Collections.emptyList();
                break;
         }


        return resultConsList;
    }


    /**
     *
     * @param contZPoint
     * @param dateTimePeriod
     * @return
     */
    private List<ZPointConsumptionVM> findHWaterCons(ContZPoint contZPoint, LocalDateTimePeriod dateTimePeriod) {

        return Collections.emptyList();
    }


}
