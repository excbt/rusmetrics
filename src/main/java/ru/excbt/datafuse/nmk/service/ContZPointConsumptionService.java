package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.QContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.QContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointConsumptionMapper;
import ru.excbt.datafuse.nmk.service.vm.ContZPointConsumptionVM;
import ru.excbt.datafuse.nmk.utils.AnyPeriod;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContZPointConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ContZPointConsumptionService.class);

    private final static QContZPointConsumption qContZPointConsumption = QContZPointConsumption.contZPointConsumption;
    private final static QContZPoint qContZPoint = QContZPoint.contZPoint;

    private final QueryDSLService queryDSLService;

    private final ContZPointConsumptionRepository consumptionRepository;

    private final ContZPointConsumptionMapper contZPointConsumptionMapper;

    private final ContZPointRepository contZPointRepository;




    @Autowired
    public ContZPointConsumptionService(QueryDSLService queryDSLService, ContZPointConsumptionRepository consumptionRepository, ContZPointConsumptionMapper contZPointConsumptionMapper, ContZPointRepository contZPointRepository) {
        this.queryDSLService = queryDSLService;
        this.consumptionRepository = consumptionRepository;
        this.contZPointConsumptionMapper = contZPointConsumptionMapper;
        this.contZPointRepository = contZPointRepository;
    }


    @Transactional(readOnly = true)
    public List<ContZPointConsumptionDTO> getConsumption(Long contZPointId,
                                                         TimeDetailKey timeDetailKey,
                                                         ConsumptionService.DataType dataType,
                                                         AnyPeriod<LocalDateTime> period) {

        if (period.isInvalidEq()) {
            log.info("period is not valid");
            return Collections.emptyList();
        }


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
     * @param contZPointId
     * @param timeDetailKey
     * @param period
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContZPointConsumptionDTO> getConsumption(Long contZPointId,
                                                         TimeDetailKey timeDetailKey,
                                                         AnyPeriod<LocalDateTime> period) {

        Optional<ConsumptionService.DataType> dataType = findDataType(contZPointId);
        if (!dataType.isPresent()) {
            return Collections.emptyList();
        }

        return getConsumption(contZPointId, timeDetailKey, dataType.get(), period);
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


    /**
     *
     * @param contZPointId
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<ConsumptionService.DataType> findDataType(Long contZPointId) {


        Tuple row = queryDSLService.queryFactory()
            .select(qContZPoint.contServiceTypeKeyname, qContZPoint.deviceObject().isImpulse)
            .from(qContZPoint)
            .where(qContZPoint.id.eq(contZPointId)).fetchOne();

        if (row == null) {
            return Optional.empty();
        }

        String contServiceType = row.get(qContZPoint.contServiceTypeKeyname);
        Optional<ContServiceTypeKey> contServiceTypeKey = KeyEnumTool.searchKey(ContServiceTypeKey.class, contServiceType);

        return contServiceTypeKey.flatMap(key -> ConsumptionService.getDataType(key, row.get(qContZPoint.deviceObject().isImpulse)));

    }

    /**
     *
     * @param localDateTimePeriod
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContZPointConsumptionVM> findAvailableYearData(LocalDateTimePeriod localDateTimePeriod) {
        return findAvailableConsumptionData(
            Arrays.asList(qContZPointConsumption.contZPointId,
            qContZPointConsumption.dataType,
            qContZPointConsumption.contServiceType,
            qContZPointConsumption.destTimeDetailType),
            localDateTimePeriod);
    }

    /**
     *
     * @param year
     * @param mon
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContZPointConsumptionVM> findAvailableMonthData(int year, int mon) {

        LocalDateTimePeriod localDateTimePeriod = LocalDateTimePeriod.month(year, mon);

        JPAQuery<Tuple> qry = queryDSLService.queryFactory()
            .select(qContZPointConsumption.contZPointId,
                qContZPointConsumption.dataType,
                qContZPointConsumption.contServiceType,
                qContZPointConsumption.destTimeDetailType,
                qContZPointConsumption.consDateTime).distinct()
            .from(qContZPointConsumption)
            .where(qContZPointConsumption.consDateTime.between(localDateTimePeriod.getFrom(), localDateTimePeriod.getTo()))
            .orderBy(qContZPointConsumption.contZPointId.asc(), qContZPointConsumption.consDateTime.asc());

        log.debug("query: {}", qry.toString());

        List<Tuple> rows = qry.fetch();
        return rows.stream().map(this::mapTupleToVM).collect(Collectors.toList());

    }

    /**
     *
     * @param localDateTimePeriod
     * @param columns
     * @return
     */
    private List<ContZPointConsumptionVM> findAvailableConsumptionData(List<Expression> columns, LocalDateTimePeriod localDateTimePeriod) {

        if (localDateTimePeriod.isInvalidEq()) {
            return Collections.emptyList();
        }

        JPAQuery<Tuple> qry = queryDSLService.queryFactory()
            .select(columns.toArray(new Expression[]{})).distinct()
            .from(qContZPointConsumption)
            .where(qContZPointConsumption.consDateTime.between(localDateTimePeriod.getFrom(), localDateTimePeriod.getTo()))
            .orderBy(qContZPointConsumption.contZPointId.asc());

        log.debug("query: {}", qry.toString());

        List<Tuple> rows = qry.fetch();
        return rows.stream().map(this::mapTupleToVM).collect(Collectors.toList());
    }


    /**
     *
     * @param tuple
     * @return
     */
    private ContZPointConsumptionVM mapTupleToVM(Tuple tuple) {
        return  ContZPointConsumptionVM.builder()
            .contZPointId(tuple.get(qContZPointConsumption.contZPointId))
            .dataType(tuple.get(qContZPointConsumption.dataType))
            .contServiceType(tuple.get(qContZPointConsumption.contServiceType))
            .timeDetailType(tuple.get(qContZPointConsumption.destTimeDetailType))
            .consDateTime(tuple.get(qContZPointConsumption.consDateTime)).build();
    }

}
