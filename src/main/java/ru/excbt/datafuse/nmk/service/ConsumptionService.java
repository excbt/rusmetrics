package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    private final ContServiceDataHWaterRepository dataHWaterRepository;

    private final ContServiceDataImpulseRepository dataImpulseRepository;

    private final ContServiceDataElConsRepository dataElConsRepository;

    private final ContZPointConsumptionRepository consumptionRepository;

    @Autowired
    public ConsumptionService(ContServiceDataHWaterRepository dataHWaterRepository, ContServiceDataImpulseRepository dataImpulseRepository, ContServiceDataElConsRepository dataElConsRepository, ContZPointConsumptionRepository consumptionRepository) {
        this.dataHWaterRepository = dataHWaterRepository;
        this.dataImpulseRepository = dataImpulseRepository;
        this.dataElConsRepository = dataElConsRepository;
        this.consumptionRepository = consumptionRepository;
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


    @Transactional(readOnly = true)
    public void processHWater(ContZPoint contZPoint, TimeDetailKey timeDetailKey, LocalDateTimePeriod period) {

        Objects.requireNonNull(contZPoint);
        Objects.requireNonNull(contZPoint.getId());
        Objects.requireNonNull(timeDetailKey);
        Objects.requireNonNull(period);

        if (period.isInvalid()) {
            throw new IllegalArgumentException("period is invalid");
        }

        Stream<ContServiceDataHWater> dataStream = dataHWaterRepository.selectForConsumption(
                                                                                contZPoint.getId(),
                                                                                timeDetailKey.getKeyname(),
                                                                                period.getFromDate(),
                                                                                period.getToDate());

        processHWaterStream (dataStream);

    }

    /**
     *
     * @param timeDetailKey
     * @param period
     */
    @Transactional(readOnly = true)
    public void processHWater(TimeDetailKey timeDetailKey, LocalDateTimePeriod period) {

        Stream<ContServiceDataHWater> dataStream = dataHWaterRepository.selectForConsumption(
            timeDetailKey.getKeyname(),
            period.getFromDate(),
            period.getToDate());

        processHWaterStream (dataStream);

    }


    private void processHWaterStream(Stream<ContServiceDataHWater> dataHWaterStream) {
        Map<Long, List<ContServiceDataHWater>> dataMap = GroupUtil.makeIdMap(dataHWaterStream, (i) -> i.getContZPoint().getId());
        log.info("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.info("Id: {}, Size: {}", i, dataMap.get(i).size()));

    }


}
