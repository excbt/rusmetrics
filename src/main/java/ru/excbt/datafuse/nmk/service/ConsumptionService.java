package ru.excbt.datafuse.nmk.service;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumptionTask;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionTaskRepository;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;
import ru.excbt.datafuse.nmk.service.handling.*;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    public static final String MD5_HASH_SECRET = "YsoB66IIyYlEFw50ObB2";
    public static final byte[] MD5_HASH_SECRET_BYTES = MD5_HASH_SECRET.getBytes(Charset.forName("UTF8"));


    public static final String CONS_STATE_CALCULATED = "calculated";
    public static final String CONS_STATE_INVALIDATED = "invalidated";

    public enum TaskState implements KeynameObject {
        NEW, STARTED, FINISHED, SCHEDULED;

        @Override
        public String getKeyname() {
            return this.name().toLowerCase();
        }
    }


    public enum DataType implements KeynameObject {
        HWATER,
        ELECTRICITY,
        IMPULSE;

        @Override
        public String getKeyname() {
            return this.name();
        }

    }

    public static final String DATA_TYPE_HWATER = DataType.HWATER.getKeyname();
    public static final String DATA_TYPE_ELECTRICITY = DataType.ELECTRICITY.getKeyname();
    public static final String DATA_TYPE_IMPULSE = DataType.IMPULSE.getKeyname();

    private final QueryDSLService queryDSLService;

    private final ContServiceDataHWaterRepository dataHWaterRepository;

    private final ContServiceDataImpulseRepository dataImpulseRepository;

    private final ContServiceDataElConsRepository dataElConsRepository;

    private final ContZPointConsumptionRepository consumptionRepository;

    private final ContZPointRepository contZPointRepository;

    private final ContZPointConsumptionTaskRepository consumptionTaskRepository;

    @Autowired
    public ConsumptionService(QueryDSLService queryDSLService,
                              ContServiceDataHWaterRepository dataHWaterRepository,
                              ContServiceDataImpulseRepository dataImpulseRepository,
                              ContServiceDataElConsRepository dataElConsRepository,
                              ContZPointConsumptionRepository consumptionRepository,
                              ContZPointRepository contZPointRepository,
                              ContZPointConsumptionTaskRepository consumptionTaskRepository) {
        this.queryDSLService = queryDSLService;
        this.dataHWaterRepository = dataHWaterRepository;
        this.dataImpulseRepository = dataImpulseRepository;
        this.dataElConsRepository = dataElConsRepository;
        this.consumptionRepository = consumptionRepository;
        this.contZPointRepository = contZPointRepository;
        this.consumptionTaskRepository = consumptionTaskRepository;
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

    /**
     *
     * @param task
     */
    @Transactional
    public void processHWater(final ConsumptionTask task) {
        processHWater (task, true);
    }

    /**
     *
     * @param task
     * @param md5Hash
     */
    @Transactional
    public void processHWater(final ConsumptionTask task, boolean md5Hash) {

        ConsumptionTaskDataLoader<ContServiceDataHWater> dataLoader = (t) -> dataHWaterRepository.selectForConsumptionAny(
            t.getSrcTimeDetailType(),
            LocalDateUtils.asDate(t.getDateTimeFrom()),
            LocalDateUtils.asDate(t.getDateTimeTo()),
            t.getContZPointId());


        internalProcessTask(task,
            dataLoader,
            Optional.empty(),
            zp -> ConsumptionFunctionLib.findHWaterFunc(zp),
            hWaterDataProcessor,
            e -> e.getContZPointId(),
            md5Hash);
    }

    /**
     *
     * @param tuple
     * @return
     */
    public static ContServiceDataElCons lastTupleDataToDataElCons(Tuple tuple) {

        QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;
        ContServiceDataElCons dataElCons = new ContServiceDataElCons();
        dataElCons.setContZPointId(tuple.get(qContServiceDataElCons.contZPointId));

        dataElCons.setP_Ap(tuple.get(qContServiceDataElCons.p_Ap.max()));
        dataElCons.setP_Ap1(tuple.get(qContServiceDataElCons.p_Ap1.max()));
        dataElCons.setP_Ap2(tuple.get(qContServiceDataElCons.p_Ap2.max()));
        dataElCons.setP_Ap3(tuple.get(qContServiceDataElCons.p_Ap3.max()));
        dataElCons.setP_Ap4(tuple.get(qContServiceDataElCons.p_Ap4.max()));

        dataElCons.setP_An(tuple.get(qContServiceDataElCons.p_An.max()));
        dataElCons.setP_An1(tuple.get(qContServiceDataElCons.p_An1.max()));
        dataElCons.setP_An2(tuple.get(qContServiceDataElCons.p_An2.max()));
        dataElCons.setP_An3(tuple.get(qContServiceDataElCons.p_An3.max()));
        dataElCons.setP_An4(tuple.get(qContServiceDataElCons.p_An4.max()));

        dataElCons.setQ_Rp(tuple.get(qContServiceDataElCons.q_Rp.max()));
        dataElCons.setQ_Rp1(tuple.get(qContServiceDataElCons.q_Rp1.max()));
        dataElCons.setQ_Rp2(tuple.get(qContServiceDataElCons.q_Rp2.max()));
        dataElCons.setQ_Rp3(tuple.get(qContServiceDataElCons.q_Rp3.max()));
        dataElCons.setQ_Rp4(tuple.get(qContServiceDataElCons.q_Rp4.max()));

        dataElCons.setQ_Rn(tuple.get(qContServiceDataElCons.q_Rn.max()));
        dataElCons.setQ_Rn1(tuple.get(qContServiceDataElCons.q_Rn1.max()));
        dataElCons.setQ_Rn2(tuple.get(qContServiceDataElCons.q_Rn2.max()));
        dataElCons.setQ_Rn3(tuple.get(qContServiceDataElCons.q_Rn3.max()));
        dataElCons.setQ_Rn4(tuple.get(qContServiceDataElCons.q_Rn4.max()));

        return dataElCons;

    }

    /**
     *
     * @param tuples
     * @return
     */
    public static List<ContServiceDataElCons> lastTupleDataToDataElCons(List<Tuple> tuples) {
        return tuples.stream().map(i -> lastTupleDataToDataElCons(i)).collect(Collectors.toList());
    }

    /**
     *
     * @param tuple
     * @return
     */
    public static ContServiceDataImpulse lastTupleDataToDataImpulse(Tuple tuple) {
        QContServiceDataImpulse qContServiceDataImpulse = QContServiceDataImpulse.contServiceDataImpulse;
        ContServiceDataImpulse dataImpulse = new ContServiceDataImpulse();
        dataImpulse.setContZPointId(tuple.get(qContServiceDataImpulse.contZPointId));
        dataImpulse.setDataValue(tuple.get(qContServiceDataImpulse.dataValue.max()));
        return dataImpulse;


    }

    /**
     *
     * @param tuples
     * @return
     */
    public static List<ContServiceDataImpulse> lastTupleDataToDataImpulse(List<Tuple> tuples) {
        return tuples.stream().map(i -> lastTupleDataToDataImpulse(i)).collect(Collectors.toList());
    }

    /**
     *
     * @return
     */
    private JPAQuery<Tuple> elDataMaxDSLQueryStarter() {

        QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;

        JPAQuery<Tuple> qry =
            queryDSLService.queryFactory().select(
                qContServiceDataElCons.contZPointId,

                qContServiceDataElCons.p_Ap.max(),
                qContServiceDataElCons.p_Ap1.max(),
                qContServiceDataElCons.p_Ap2.max(),
                qContServiceDataElCons.p_Ap3.max(),
                qContServiceDataElCons.p_Ap4.max(),

                qContServiceDataElCons.p_An.max(),
                qContServiceDataElCons.p_An1.max(),
                qContServiceDataElCons.p_An2.max(),
                qContServiceDataElCons.p_An3.max(),
                qContServiceDataElCons.p_An4.max(),

                qContServiceDataElCons.q_Rp.max(),
                qContServiceDataElCons.q_Rp1.max(),
                qContServiceDataElCons.q_Rp2.max(),
                qContServiceDataElCons.q_Rp3.max(),
                qContServiceDataElCons.q_Rp4.max(),

                qContServiceDataElCons.q_Rn.max(),
                qContServiceDataElCons.q_Rn1.max(),
                qContServiceDataElCons.q_Rn2.max(),
                qContServiceDataElCons.q_Rn3.max(),
                qContServiceDataElCons.q_Rn4.max()
            )
                .from(qContServiceDataElCons);

        return qry;
    }


    @Transactional
    public void processElCons(final ConsumptionTask task) {

        ConsumptionTaskDataLoader<ContServiceDataElCons> dataLoader = (t) -> dataElConsRepository.selectForConsumptionAny(
            t.getSrcTimeDetailType(),
            LocalDateUtils.asDate(t.getDateTimeFrom()),
            LocalDateUtils.asDate(t.getDateTimeTo()),
            t.getContZPointId());


        ConsumptionTaskLastDataLoader<ContServiceDataElCons> lastDataLoader = (t, ids) -> {
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;
            List<Tuple> resultTupleList = elDataMaxDSLQueryStarter()
                .where(
                    qContServiceDataElCons.contZPointId.in(ids)
                        .and(qContServiceDataElCons.timeDetailType.eq(task.getSrcTimeDetailType()))
                        .and(qContServiceDataElCons.deleted.eq(0))
                        .and(qContServiceDataElCons.dataDate.lt(LocalDateUtils.asDate(task.getDateTimeFrom())))
                ).groupBy(qContServiceDataElCons.contZPointId).fetch();
            return lastTupleDataToDataElCons(resultTupleList);
        };

        internalProcessTask(task,
            dataLoader,
            Optional.of(lastDataLoader),
            zp -> ConsumptionFunctionLib.findElConsFunc(zp),
            elConsDataProcessor,
            e -> e.getContZPointId(),
            true);

    }


    @Transactional
    public void processImpulse(final ConsumptionTask task) {

        ConsumptionTaskDataLoader<ContServiceDataImpulse> dataLoader = (t) -> dataImpulseRepository.selectForConsumptionAny(
            t.getSrcTimeDetailType(),
            LocalDateUtils.asDate(t.getDateTimeFrom()),
            LocalDateUtils.asDate(t.getDateTimeTo()),
            t.getContZPointId());


        ConsumptionTaskLastDataLoader<ContServiceDataImpulse> lastDataLoader = (t, ids) -> {
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            QContServiceDataImpulse qContServiceDataImpulse = QContServiceDataImpulse.contServiceDataImpulse;
            List<Tuple> resultTuple = queryDSLService.queryFactory()
                .select(
                qContServiceDataImpulse.contZPointId,
                qContServiceDataImpulse.dataValue.max()
            ).from(qContServiceDataImpulse).where(
                qContServiceDataImpulse.contZPointId.in(ids)
                    .and(qContServiceDataImpulse.timeDetailType.eq(task.getSrcTimeDetailType()))
                    .and(qContServiceDataImpulse.deleted.eq(0))
                    .and(qContServiceDataImpulse.dataDate.lt(LocalDateUtils.asDate(task.getDateTimeFrom())))
            ).groupBy(qContServiceDataImpulse.contZPointId).fetch();
            return lastTupleDataToDataImpulse(resultTuple);
        };


        internalProcessTask(
            task,
            dataLoader,
            Optional.of(lastDataLoader),
            zp -> ConsumptionFunctionLib.findImpulseFunc(zp),
            impulseDataProcessor,
            e -> e.getContZPointId(),
            true);

    }

    /**
     *
     * @return
     */
    private MessageDigest getMd5HashDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    private String calcMd5Hash (double[] data) {
        if (data == null) {
            return null;
        }

        ByteBuffer bb = ByteBuffer.allocate(data.length * 8);
        for(double d : data) {
            bb.putDouble(d);
        }
        byte[] bytes = bb.array();
        MessageDigest md = getMd5HashDigest();
        md.update(bytes);
        md.update(MD5_HASH_SECRET_BYTES);
        byte[] digest = md.digest();
        String myChecksum = DatatypeConverter
            .printHexBinary(digest).toUpperCase();
        return myChecksum;
    }


    /**
     *
     * @param consumption
     * @return
     */
    private String calcMd5Hash (ContZPointConsumption consumption) {
        final double[] values = consumption.getConsData();
        if (values == null || values.length == 0) {
            return null;
        }
        int len = values.length;
        if (consumption.getDataInAbs() != null) len ++;
        if (consumption.getDataOutAbs() != null) len ++;

        double[] valuesFull = new double[len];

        for (int i = 0; i < values.length; i++) {
            valuesFull[i] = values[i];
        }
        int posCnt = values.length - 1;
        if (consumption.getDataInAbs() != null) valuesFull[posCnt++] = consumption.getDataInAbs();
        if (consumption.getDataOutAbs() != null) valuesFull[posCnt++] = consumption.getDataOutAbs();

        return calcMd5Hash(valuesFull);
    }


    /**
     *
     */
    @Getter
    @EqualsAndHashCode
    public static class ConsumptionZPointKey {
        private final Long contZPointId;
        private final String destTimeDetailType;
        private final LocalDateTime consDateTime;

        public ConsumptionZPointKey(Long contZPointId, String destTimeDetailType, LocalDateTime consDateTime) {
            Objects.requireNonNull(contZPointId);
            Objects.requireNonNull(destTimeDetailType);
            Objects.requireNonNull(consDateTime);

            this.contZPointId = contZPointId;
            this.destTimeDetailType = destTimeDetailType;
            this.consDateTime = consDateTime;
        }

        public ConsumptionZPointKey(Long contZPointId, String destTimeDetailType, LocalDate consDate) {
            Objects.requireNonNull(contZPointId);
            Objects.requireNonNull(destTimeDetailType);
            Objects.requireNonNull(consDate);

            this.contZPointId = contZPointId;
            this.destTimeDetailType = destTimeDetailType;
            this.consDateTime = consDate.atStartOfDay();
        }
    }


    /**
     *
     */
    @Getter
    @EqualsAndHashCode
    public static class ConsumptionDataTypeKey {

        private final String dataType;
        private final String destTimeDetailType;
        private final LocalDateTime consDateTime;

        public ConsumptionDataTypeKey(String dataType, String destTimeDetailType, LocalDateTime consDateTime) {
            Objects.requireNonNull(dataType);
            Objects.requireNonNull(destTimeDetailType);
            Objects.requireNonNull(consDateTime);

            if (!KeyEnumTool.checkKeys(TimeDetailKey.class, destTimeDetailType)) {
                throw new IllegalArgumentException("destTimeDetailType is invalid");
            }

            if (!KeyEnumTool.checkKeys(DataType.class, dataType)) {
                throw new IllegalArgumentException("dataType is invalid");
            }

            this.dataType = dataType;
            this.destTimeDetailType = destTimeDetailType;
            this.consDateTime = consDateTime;
        }

        public ConsumptionDataTypeKey(String dataType, String destTimeDetailType, LocalDate consDate) {

            this(dataType,
                destTimeDetailType,
                (consDate == null) ? null : consDate.atStartOfDay());
        }

    }


    /**
     *
     * @param cleanKeys
     */
    private void cleanConsumptionZPoint(Collection<ConsumptionZPointKey> cleanKeys) {
        log.debug("Cleaning {} keys", cleanKeys.size());
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByKey(key.getContZPointId(), key.getDestTimeDetailType(), key.getConsDateTime());
        });
    }

    /**
     *
     * @param cleanKeys
     */
    @Transactional
    public void invalidateConsumption(Collection<ConsumptionZPointKey> cleanKeys) {
        log.debug("Updating {} keys", cleanKeys.size());
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.updateStateByKey(CONS_STATE_INVALIDATED, key.getContZPointId(), key.getDestTimeDetailType(), key.getConsDateTime());
        });
    }

    /**
     *
     * @param cleanKeys
     */
    @Transactional
    public void invalidateConsumption(ConsumptionZPointKey ... cleanKeys) {
        log.debug("Updating {} keys", cleanKeys.length);
        for (ConsumptionZPointKey key: cleanKeys) {
            consumptionRepository.updateStateByKey(CONS_STATE_INVALIDATED, key.getContZPointId(), key.getDestTimeDetailType(), key.getConsDateTime());
        }
    }

    /**
     *
     * @param cleanKeys
     */
    private void cleanConsumptionDataType(Collection<ConsumptionDataTypeKey> cleanKeys) {
        log.debug("Cleaning {} keys", cleanKeys.size());
        log.debug("Clean consumption by dataType in progress");
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByDataTypeKey(key.getDataType(), key.getDestTimeDetailType(), key.getConsDateTime());
        });
        log.debug("Clean consumption by dataType complete");
    }


    /**
     *
     * @param consumptionTask
     * @param taskState
     * @return
     */
    @Transactional
    public ConsumptionTask saveConsumptionTask(final ConsumptionTask consumptionTask, final String taskState) {

        ContZPointConsumptionTask contZPointConsumptionTask = new ContZPointConsumptionTask();
        contZPointConsumptionTask.setTaskState(taskState == null ? TaskState.NEW.name() : taskState);
        contZPointConsumptionTask.setTaskDateTime(Instant.now());
        contZPointConsumptionTask.setConsDateTime(consumptionTask.getDateTimeFrom());
        contZPointConsumptionTask.setDataType(consumptionTask.getDataType());
        contZPointConsumptionTask.setTaskStateDateTime(contZPointConsumptionTask.getTaskDateTime());
        contZPointConsumptionTask.setContZPointId(consumptionTask.getContZPointId());
        contZPointConsumptionTask.setTaskUUID(consumptionTask.getTaskUUID() != null ? consumptionTask.getTaskUUID() : Generators.timeBasedGenerator().generate());
        consumptionTaskRepository.save(contZPointConsumptionTask);

        return consumptionTask.newTaskUUID(contZPointConsumptionTask.getTaskUUID());
    }


    /**
     *
     * @param consumptionTask
     * @param taskState
     * @return
     */
    @Transactional
    public Boolean updateTaskState(final ConsumptionTask consumptionTask, TaskState taskState) {
        return updateTaskStateInt(consumptionTask, taskState, Optional.empty(), Optional.empty());
    }

    /**
     *
     * @param consumptionTask
     * @param taskState
     * @param expectedState
     * @return
     */
    @Transactional
    public Boolean updateTaskState(final ConsumptionTask consumptionTask, TaskState taskState, Optional<TaskState> expectedState) {
        return updateTaskStateInt (consumptionTask, taskState, expectedState, Optional.empty());
    }


    private class TaskDataCounter {

        final Long inCount;
        final Long outCount;

        public TaskDataCounter(Long inCount, Long outCount) {
            this.inCount = inCount;
            this.outCount = outCount;
        }

        public TaskDataCounter(Integer inCount, Integer outCount) {
            this.inCount = Long.valueOf(inCount);
            this.outCount = Long.valueOf(outCount);
        }

        private boolean notNullValues() {
            return inCount != null && outCount != null;
        }
    }

    /**
     *
     * @param consumptionTask
     * @param taskState
     * @param expectedState
     * @param taskDataCounter
     * @return
     */
    private Boolean updateTaskStateInt(final ConsumptionTask consumptionTask,
                                       final TaskState taskState,
                                       final Optional<TaskState> expectedState,
                                       final Optional<TaskDataCounter> taskDataCounter) {
        if (consumptionTask.getTaskUUID() == null) {
            return null;
        }

        Objects.requireNonNull(expectedState);

        Optional<ContZPointConsumptionTask> task = consumptionTaskRepository.findByTaskUUID(consumptionTask.getTaskUUID()).stream().findFirst();
        Boolean result = task.map( i -> {

                if (expectedState.isPresent() && !expectedState.get().getKeyname().equals(i.getTaskState())) {
                    return false;
                    //throw new IllegalStateException("Illegal task status");
                }

                i.setTaskState(taskState.getKeyname());
                i.setTaskStateDateTime(Instant.now());
                if (taskState == TaskState.STARTED) {
                    i.setStartDateTime(Instant.now());
                } else if (taskState == TaskState.FINISHED) {
                    i.setFinishDateTime(Instant.now());
                    taskDataCounter.filter(TaskDataCounter::notNullValues).ifPresent(cnt -> {
                        i.setInCount(cnt.inCount);
                        i.setOutCount(cnt.outCount);
                    });
                }
                consumptionTaskRepository.save(i);
                return true;
            }
        ).orElse(null);

        return result;
    }

    @FunctionalInterface
    public interface ConsumptionDataProcessor<T> {
        Boolean apply(ContZPointConsumption consumption, List<T> inData, Optional<List<T>> preData, ConsumptionFunction<T> consFunc);
    }


    /**
     *
     * @param <T>
     */
    public static abstract class ConsumptionDataProcessorAbs<T> implements ConsumptionDataProcessor<T> {

        public abstract Comparator<T> getDataComparator();

        @Override
        public Boolean apply(ContZPointConsumption consumption, List<T> inData, Optional<List<T>> preData, ConsumptionFunction<T> consFunc) {

            Optional<Double> periodLastValue = ConsumptionFunctionLib.lastValue(inData, getDataComparator(), consFunc);

            if (!periodLastValue.isPresent()) {
                return false;
            }

            Optional<Double> prePeriodLastValue = preData.map(d -> ConsumptionFunctionLib.lastValue(d, getDataComparator(), consFunc).orElse(null));
            double[] consValues = null;

            // periodLastValue.isPresent() is always true
            if (prePeriodLastValue.isPresent()) {

                double absValue = Math.abs(periodLastValue.get() - prePeriodLastValue.get());

                consValues = new double[1];
                consValues[0] = absValue;

                if (absValue == 0) {
                    return false;
                }
            }

            if (consValues == null) {
                return false;
            }

            consumption.setConsValueName(consFunc.getValueName());
            consumption.setDataInAbs(prePeriodLastValue.get());
            consumption.setDataOutAbs(periodLastValue.get());
            consumption.setConsData(consValues);
            consumption.setMeasureUnit(consFunc.getMeasureUnit());

            return true;
        }
    }

    /**
     *
     * @param <T>
     */
    public static abstract class ConsumptionDataProcessorArr<T> implements ConsumptionDataProcessor<T> {

        public abstract Comparator<T> getDataComparator();

        @Override
        public Boolean apply(ContZPointConsumption consumption, List<T> inData, Optional<List<T>> preData, ConsumptionFunction<T> consFunc) {

            double[] consValues = ConsumptionFunctionLib.allValues(inData, getDataComparator(), consFunc);

            double sum = 0;
            for (int i = 0; i < consValues.length; i++) {
                sum = sum + consValues[i];
            }

            if (consValues.length == 0 || sum == 0) {
                return false;
            }

            consumption.setConsValueName(consFunc.getValueName());
            consumption.setDataInAbs(null);
            consumption.setDataOutAbs(null);
            consumption.setConsData(consValues);
            consumption.setMeasureUnit(consFunc.getMeasureUnit());

            return true;
        }
    }


    private final ConsumptionDataProcessorArr<ContServiceDataHWater> hWaterDataProcessor = new ConsumptionDataProcessorArr<ContServiceDataHWater>() {
        @Override
        public Comparator<ContServiceDataHWater> getDataComparator() {
            return Comparator.comparing(ContServiceDataHWater::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    };


    private final ConsumptionDataProcessorAbs<ContServiceDataElCons> elConsDataProcessor = new ConsumptionDataProcessorAbs<ContServiceDataElCons>() {
        @Override
        public Comparator<ContServiceDataElCons> getDataComparator() {
            return Comparator.comparing(ContServiceDataElCons::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    };

    private final ConsumptionDataProcessorAbs<ContServiceDataImpulse> impulseDataProcessor = new ConsumptionDataProcessorAbs<ContServiceDataImpulse>() {
        @Override
        public Comparator<ContServiceDataImpulse> getDataComparator() {
            return Comparator.comparing(ContServiceDataImpulse::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    };


    /**
     *
     * @param task
     * @param inDataList
     * @param optPrePeriodLastDataLoader
     * @param entityIdExtractor
     * @param consumptionDataProcessor
     * @param consumptionFunctionSupplier
     * @param md5Hash
     * @param <T>
     * @return
     */
    private <T> List<ContZPointConsumption> processAnyListUniversal(final ConsumptionTask task,
                                                                    final List<T> inDataList,
                                                                    Optional<ConsumptionTaskLastDataLoader <T>> optPrePeriodLastDataLoader,
                                                                    EntityIdExtractor<T> entityIdExtractor,
                                                                    ConsumptionDataProcessor<T> consumptionDataProcessor,
                                                                    ConsumptionFunctionSupplier<T> consumptionFunctionSupplier,
                                                                    boolean md5Hash) {

        Objects.requireNonNull(task);

        Optional<ContZPointConsumptionTask> optContZPointConsumptionTask = task.getTaskUUID() == null ? Optional.empty() :
            consumptionTaskRepository.findByTaskUUID(task.getTaskUUID()).stream().findAny();

        Boolean taskStateUpdateResult = updateTaskState (task, TaskState.STARTED, Optional.of(TaskState.SCHEDULED));
        if (Boolean.FALSE.equals(taskStateUpdateResult)) {
            throw new IllegalStateException("Expecting state of task is not " + TaskState.SCHEDULED);
        }

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(new ConsumptionDataTypeKey(task.getDataType(), task.getDestTimeDetailType(), task.getDateFrom()));

        cleanConsumptionDataType(cleanKeys2);

        if (inDataList.isEmpty()) {
            updateTaskState (task, TaskState.FINISHED);
            return Collections.emptyList();
        }

        final Map<Long, List<T>> periodDataMap = GroupUtil.makeIdMap(inDataList, (e) -> entityIdExtractor.getId(e));

        log.trace("ContZPoints: ");
        periodDataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, periodDataMap.get(i).size()));

        final Optional<Long> optTaskId = optContZPointConsumptionTask.map(i -> i.getId());

        List<Long> contZPointIds = periodDataMap.keySet().stream().distinct().collect(Collectors.toList());
        List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);
        Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        optPrePeriodLastDataLoader.ifPresent(i -> log.debug("Pre period Data fetching"));
        Optional<List<T>> optPrePeriodLastDataDataAll = optPrePeriodLastDataLoader.map(i -> i.load(task, contZPointIds));
        optPrePeriodLastDataLoader.ifPresent(i -> log.debug("Pre period Data fetching complete"));

        if (optPrePeriodLastDataDataAll.isPresent()) {
            if (optPrePeriodLastDataDataAll.get() == null ||
                optPrePeriodLastDataDataAll.get().isEmpty()) {
                updateTaskState (task, TaskState.FINISHED);
                return Collections.emptyList();
            }
        }

        Optional<Map<Long, List<T>>> optPrePeriodLastDataDataMap = optPrePeriodLastDataDataAll
            .map(i -> GroupUtil.makeIdMap(optPrePeriodLastDataDataAll.get(), e -> entityIdExtractor.getId(e)));

        log.debug("Starting loop for: {} keys", periodDataMap.keySet().size());

        List<ContZPointConsumption> consumptionDataList = new ArrayList<>();

        // For each ContZPoint
        periodDataMap.keySet().stream().sorted().forEach(id -> {
            ContZPoint contZPoint = contZPointMap.get(id);
            if (contZPoint == null) {
                log.warn("ContZPoint (id={}) is not found", id);
                return;
            }
            List<T> periodData = periodDataMap.get(id);

            List<ConsumptionFunction<T>> consumptionFunctions = consumptionFunctionSupplier.supply(contZPoint);

            for (ConsumptionFunction<T> consFunc: consumptionFunctions) {

                ContZPointConsumption consumption = new ContZPointConsumption();

                Boolean dataProcessorResult =  consumptionDataProcessor.apply(
                                                consumption,
                                                periodData,
                                                optPrePeriodLastDataDataMap.map(i -> i.get(id)) ,
                                                consFunc );

                if (Boolean.FALSE.equals(dataProcessorResult)) {
                    continue;
                }

                consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
                consumption.setContZPointId(id);
                consumption.setDataType(task.getDataType());
                consumption.setConsDateTime(task.getDateTimeFrom());
                consumption.setSrcTimeDetailType(task.getSrcTimeDetailType());
                consumption.setDestTimeDetailType(task.getDestTimeDetailType());
                consumption.setDateTimeFrom(task.getDateTimeFrom());
                consumption.setDateTimeTo(task.getDateTimeTo());
                consumption.setConsState(CONS_STATE_CALCULATED);
                optTaskId.ifPresent(i -> consumption.setConsTaskId(i));

                if (md5Hash) {
                    String hash = calcMd5Hash(consumption);
                    consumption.setConsMD5(hash);
                }
                log.trace("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consumption.getConsData().hashCode(), consumption.getConsMD5());
                consumptionDataList.add(consumption);
            }
        });

        log.debug("End loop for: {} keys", periodDataMap.keySet().size());

        consumptionRepository.save(consumptionDataList);
        consumptionRepository.flush();

        TaskDataCounter counter = new TaskDataCounter(inDataList.size(), consumptionDataList.size());

        updateTaskStateInt (task, TaskState.FINISHED, Optional.empty(), Optional.of(counter));

        return consumptionDataList;
    }


    /**
     *
     * @param task
     * @param taskDataLoader
     * @param optTaskLastDataLoader
     * @param consumptionFunctionSupplier
     * @param consumptionDataProcessor
     * @param idExtractor
     * @param md5hash
     * @param <T>
     */
    private <T> void internalProcessTask(final ConsumptionTask task,
                                         final ConsumptionTaskDataLoader<T> taskDataLoader,
                                         final Optional<ConsumptionTaskLastDataLoader<T>> optTaskLastDataLoader,
                                         final ConsumptionFunctionSupplier<T> consumptionFunctionSupplier,
                                         final ConsumptionDataProcessor<T> consumptionDataProcessor,
                                         final EntityIdExtractor<T> idExtractor,
                                         boolean md5hash) {
        Objects.requireNonNull(task);

        if (!task.isValid()) {
            throw new IllegalArgumentException("Task is invalid");
        }

        if (!KeyEnumTool.checkNames(DataType.class, task.getDataType())) {
            throw new IllegalArgumentException("Data type is invalid: " + task.getDataType());
        }

        //final ConsumptionTask workTask = task.newIfNoUUID();
        final ConsumptionTask workTask = task;

        if (!workTask.isValid()) {
            log.warn("Task is invalid");
            return;
        }

        log.debug("Processing task: {}" ,workTask.toString());

        TimeDetailKey srcTimeDetailKey = TimeDetailKey.searchKeyname(workTask.getSrcTimeDetailType());
        //InstantPeriod period = InstantPeriod.builder().dateTimeFrom(workTask.getFrom()).dateTimeTo(workTask.getTo()).build();
        LocalDateTimePeriod localPeriod = LocalDateTimePeriod.builder()
            .dateTimeFrom(workTask.getDateTimeFrom()).dateTimeTo(workTask.getDateTimeTo()).build();

        if (srcTimeDetailKey != null && localPeriod.isValid()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            List<T> data = taskDataLoader.load(workTask);

            stopWatch.stop();
            log.debug("task data loaded in {}", stopWatch.toString());
            stopWatch.reset();
            stopWatch.start();

            processAnyListUniversal(
                task,
                data,
                optTaskLastDataLoader,
                d -> idExtractor.getId(d),
                consumptionDataProcessor,
                zp -> consumptionFunctionSupplier.supply(zp),
                md5hash
            );
            stopWatch.stop();
            log.debug("task data processed in {}", stopWatch.toString());
        } else {
            log.warn("ConsumptionTask is invalid: {}", task);
        }

    }


    public static Optional<DataType> getDataType (ContServiceTypeKey contServiceTypeKey, Boolean isImpulse) {

        if (Boolean.TRUE.equals(isImpulse)) {
            return Optional.of(DataType.IMPULSE);
        }

        if (EnumSet.of(
            ContServiceTypeKey.HEAT,
            ContServiceTypeKey.CW,
            ContServiceTypeKey.HW).contains(contServiceTypeKey)) {
            return Optional.of(DataType.HWATER);
        }

        if (EnumSet.of(
            ContServiceTypeKey.EL).contains(contServiceTypeKey)) {
            return Optional.of(DataType.ELECTRICITY);
        }

        return Optional.empty();
    }


}
