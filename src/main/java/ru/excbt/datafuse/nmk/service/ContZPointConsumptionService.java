package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.InstantPeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.QContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointConsumptionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContZPointConsumptionService {

    private final QueryDSLService queryDSLService;

    private final ContZPointConsumptionRepository consumptionRepository;

    private final ContZPointConsumptionMapper contZPointConsumptionMapper;

    @Autowired
    public ContZPointConsumptionService(QueryDSLService queryDSLService, ContZPointConsumptionRepository consumptionRepository, ContZPointConsumptionMapper contZPointConsumptionMapper) {
        this.queryDSLService = queryDSLService;
        this.consumptionRepository = consumptionRepository;
        this.contZPointConsumptionMapper = contZPointConsumptionMapper;
    }


    @Transactional
    public List<ContZPointConsumptionDTO> getConsumption(Long contZPointId,
                                                         TimeDetailKey timeDetailKey,
                                                         ConsumptionService.DataType dataType,
                                                         LocalDateTimePeriod period) {

        QContZPointConsumption qContZPointConsumption = QContZPointConsumption.contZPointConsumption;

        Predicate expression = qContZPointConsumption.contZPointId.eq(contZPointId)
            .and(qContZPointConsumption.consDateTime.between(period.getDateTimeFrom(), period.getDateTimeTo()))
            .and(qContZPointConsumption.destTimeDetailType.eq(timeDetailKey.getKeyname()))
            .and(qContZPointConsumption.consState.eq(ConsumptionService.CONS_STATE_CALCULATED))
            .and(qContZPointConsumption.dataType.eq(dataType.getKeyname()));

        Iterable<ContZPointConsumption> resultIterator = consumptionRepository.findAll(expression);


        List<ContZPointConsumptionDTO> resultDTOList = StreamSupport.stream(resultIterator.spliterator(), false)
            .map(i -> processSum(contZPointConsumptionMapper.toDto(i), i))
            .collect(Collectors.toList());


        return resultDTOList;

    }


    /**
     *
     * @param values
     * @return
     */
    private Optional<Double> sumDataValues(double[] values) {
        if (values == null) {
            Optional.empty();
        }
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[0];
        }

        sum = ConsumptionFunction.roundScale(sum, ConsumptionFunction.DEFAULT_ROUND_SCALE, ConsumptionFunction.DEFAULT_ROUND_MODE);

        return Optional.of(sum);
    }


    private ContZPointConsumptionDTO processSum(ContZPointConsumptionDTO dto, ContZPointConsumption consumption) {
        if (consumption == null) {
            return dto;
        }

        sumDataValues(consumption.getConsData()).ifPresent(dto::setConsValue);

        return dto;

    }

}
