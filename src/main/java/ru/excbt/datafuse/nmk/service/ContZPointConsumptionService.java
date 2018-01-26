package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.QContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointConsumptionMapper;
import ru.excbt.datafuse.nmk.utils.AnyPeriod;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContZPointConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ContZPointConsumptionService.class);

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
                                                         AnyPeriod<LocalDateTime> period) {

        if (period.isInvalidEq()) {
            log.info("period is not valid");
            return Collections.emptyList();
        }

        QContZPointConsumption qContZPointConsumption = QContZPointConsumption.contZPointConsumption;

        Predicate expression = qContZPointConsumption.contZPointId.eq(contZPointId)
            .and(qContZPointConsumption.consDateTime.between(period.getFrom(), period.getTo()))
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
