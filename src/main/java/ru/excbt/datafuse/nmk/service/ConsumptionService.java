package ru.excbt.datafuse.nmk.service;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.QContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.support.InstantPeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.service.DBSessionService;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumptionTask;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionTaskRepository;
import ru.excbt.datafuse.nmk.repository.DataElConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    public static final String MD5_HASH_SECRET = "YsoB66IIyYlEFw50ObB2";
    public static final byte[] MD5_HASH_SECRET_BYTES = MD5_HASH_SECRET.getBytes(Charset.forName("UTF8"));

    public static final String CONS_STATE_CALCULATED = "CALCULATED";
    public static final String CONS_STATE_INVALIDATED = "INVALIDATED";

    public static final String TASK_STATE_CALCULATING = "CALCULATING";
    public static final String TASK_STATE_FINISHED = "FINISHED";
    public static final String TASK_STATE_SCHEDULED = "SCHEDULED";
    public static final String TASK_STATE_NEW = "NEW";


    public static final String DATA_TYPE_HWATER = "HWATER";
    public static final String DATA_TYPE_ELECTRICITY = "ELECTRICITY";

    private final DBSessionService dbSessionService;

    private final ContServiceDataHWaterRepository dataHWaterRepository;

    private final ContServiceDataImpulseRepository dataImpulseRepository;

    private final DataElConsumptionRepository dataElConsRepository;

    private final ContZPointConsumptionRepository consumptionRepository;

    private final ContZPointRepository contZPointRepository;

    private final ContZPointConsumptionTaskRepository consumptionTaskRepository;

    @Autowired
    public ConsumptionService(DBSessionService dbSessionService, ContServiceDataHWaterRepository dataHWaterRepository,
                              ContServiceDataImpulseRepository dataImpulseRepository,
                              DataElConsumptionRepository dataElConsRepository,
                              ContZPointConsumptionRepository consumptionRepository,
                              ContZPointRepository contZPointRepository,
                              ContZPointConsumptionTaskRepository consumptionTaskRepository) {
        this.dbSessionService = dbSessionService;
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


    @Transactional
    public void processHWater(final ConsumptionTask task, ContZPoint contZPoint, boolean md5Hash) {

        Objects.requireNonNull(task);

        final ConsumptionTask workTask = task;

        Objects.requireNonNull(contZPoint);
        Objects.requireNonNull(contZPoint.getId());
        Objects.requireNonNull(workTask);

        DateInterval period = workTask.toDateInterval();

        if (period.isInvalid()) {
            throw new IllegalArgumentException("period is invalid");
        }

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumptionOne(
                                                                                contZPoint.getId(),
            workTask.getSrcTimeDetailType(),
            period.getFromDate(),
            period.getToDate());


        processAnyListUniversal(task,
            data,
            Optional.empty(),
            d -> d.getContZPointId(),
            hWaterDataProcessor,
            zp -> ConsumptionFunctionLib.findHWaterFunc(zp),
            md5Hash
        );


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
        Objects.requireNonNull(task);

        //final ConsumptionTask workTask = task.newIfNoUUID();
        final ConsumptionTask workTask = task;

        if (!workTask.isValid()) {
            log.warn("Task is invalid");
            return;
        }

        log.debug("Processing task: {}" ,workTask.toString());

        TimeDetailKey srcTimeDetailKey = TimeDetailKey.searchKeyname(workTask.getSrcTimeDetailType());
        InstantPeriod period = InstantPeriod.builder().dateTimeFrom(workTask.getDateTimeFrom()).dateTimeTo(workTask.getDateTimeTo()).build();

        if (srcTimeDetailKey != null && period.isValid()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            List<ContServiceDataHWater> dataHWaterList = dataHWaterRepository.selectForConsumptionAny(
                srcTimeDetailKey.getKeyname(),
                period.getFromDate(),
                period.getToDate(),
                workTask.getContZPointId());

            stopWatch.stop();
            log.debug("task data loaded in {}", stopWatch.toString());
            stopWatch.reset();
            stopWatch.start();

            processAnyListUniversal(task,
                dataHWaterList,
                Optional.empty(),
                d -> d.getContZPointId(),
                hWaterDataProcessor,
                zp -> ConsumptionFunctionLib.findHWaterFunc(zp),
                md5Hash
            );

            stopWatch.stop();
            log.debug("task data processed in {}", stopWatch.toString());
        } else {
            log.warn("ConsumptionTask is invalid: {}", task);
        }

    }

    /**
     *
     * @param tuple
     * @return
     */
    public static ContServiceDataElCons lastTupleDataToDalaElCons(Tuple tuple) {

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
    public static List<ContServiceDataElCons> tuppleToDalaElCons(List<Tuple> tuples) {
        return tuples.stream().map(i -> lastTupleDataToDalaElCons(i)).collect(Collectors.toList());
    }

    /**
     *
     * @return
     */
    private JPAQuery<Tuple> elDataMaxDSLQueryStarter() {

        QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;

        JPAQuery<Tuple> qry =
            dbSessionService.jpaQueryFactory().select(
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
        Objects.requireNonNull(task);

        //final ConsumptionTask workTask = task.newIfNoUUID();
        final ConsumptionTask workTask = task;

        if (!workTask.isValid()) {
            log.warn("Task is invalid");
            return;
        }

        log.debug("Processing task: {}" ,workTask.toString());

        TimeDetailKey srcTimeDetailKey = TimeDetailKey.searchKeyname(workTask.getSrcTimeDetailType());
        InstantPeriod period = InstantPeriod.builder().dateTimeFrom(workTask.getDateTimeFrom()).dateTimeTo(workTask.getDateTimeTo()).build();

        if (srcTimeDetailKey != null && period.isValid()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            List<ContServiceDataElCons> data = dataElConsRepository.selectForConsumptionAny(
                srcTimeDetailKey.getKeyname(),
                period.getFromDate(),
                period.getToDate(),
                workTask.getContZPointId());

            stopWatch.stop();
            log.debug("task data loaded in {}", stopWatch.toString());
            stopWatch.reset();
            stopWatch.start();

            Function<List<Long>, List<ContServiceDataElCons>> prePeriodLastDataLoader = ids -> {

                if (ids == null || ids.isEmpty()) {
                    return Collections.emptyList();
                }

                QContServiceDataElCons qContServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;
                List<Tuple> resultTupleList = elDataMaxDSLQueryStarter()
                    .where(
                        qContServiceDataElCons.contZPointId.in(ids)
                            .and(qContServiceDataElCons.timeDetailType.eq(task.getSrcTimeDetailType()))
                            .and(qContServiceDataElCons.deleted.eq(0))
                            .and(qContServiceDataElCons.dataDate.lt(Date.from(task.getDateTimeFrom())))
                    ).groupBy(qContServiceDataElCons.contZPointId).fetch();
                return tuppleToDalaElCons(resultTupleList);
            };

            processAnyListUniversal(task,
                            data,
                            Optional.of(prePeriodLastDataLoader),
                            d -> d.getContZPointId(),
                            elConsDataProcessor,
                            zp -> ConsumptionFunctionLib.findElConsFunc(zp),
                            true
                            );
            stopWatch.stop();
            log.debug("task data processed in {}", stopWatch.toString());
        } else {
            log.warn("ConsumptionTask is invalid: {}", task);
        }

    }


    /**
     *
     * @return
     */
    private MessageDigest md5Hash() {
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
        MessageDigest md = md5Hash();
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
    @Builder
    @EqualsAndHashCode
    public static class ConsumptionZPointKey {
        private final Long contZPointId;
        private final String destTimeDetailType;
        private final Instant contDateTime;
    }


    /**
     *
     */
    @Getter
    @Builder
    @EqualsAndHashCode
    public static class ConsumptionDataTypeKey {
        private final String dataType;
        private final String destTimeDetailType;
        private final Instant consDateTime;
    }


    /**
     *
     * @param cleanKeys
     */
    private void cleanConsumptionZPoint(Collection<ConsumptionZPointKey> cleanKeys) {
        log.debug("Cleaning {} keys", cleanKeys.size());
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByKey(key.getContZPointId(), key.getDestTimeDetailType(), key.getContDateTime());
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
            consumptionRepository.updateStateByKey(CONS_STATE_INVALIDATED, key.getContZPointId(), key.getDestTimeDetailType(), key.getContDateTime());
        });
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
        contZPointConsumptionTask.setTaskState(taskState == null ? TASK_STATE_NEW : taskState);
        contZPointConsumptionTask.setTaskDateTime(Instant.now());
        contZPointConsumptionTask.setConsDateTime(consumptionTask.getDateTimeFrom());
        contZPointConsumptionTask.setDataType(consumptionTask.getDataType());
        contZPointConsumptionTask.setTaskStateDt(contZPointConsumptionTask.getTaskDateTime());
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
    public Boolean updateTaskState(final ConsumptionTask consumptionTask, String taskState) {
        return updateTaskState(consumptionTask, taskState, null);
    }

    /**
     *
     * @param consumptionTask
     * @param taskState
     * @param expectedState
     * @return
     */
    @Transactional
    public Boolean updateTaskState(final ConsumptionTask consumptionTask, String taskState, String expectedState) {
        if (consumptionTask.getTaskUUID() == null) {
            return null;
        }

        Optional<ContZPointConsumptionTask> task = consumptionTaskRepository.findByTaskUUID(consumptionTask.getTaskUUID()).stream().findFirst();
        Boolean result = task.map( i -> {

                if (expectedState != null && !expectedState.equals(i.getTaskState())) {
                    return false;
                    //throw new IllegalStateException("Illegal task status");
                }

                i.setTaskState(taskState);
                i.setTaskStateDt(Instant.now());
                consumptionTaskRepository.save(i);
                return true;
            }
        ).orElse(null);

        return result;
    }

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


    private ConsumptionDataProcessorArr<ContServiceDataHWater> hWaterDataProcessor = new ConsumptionDataProcessorArr<ContServiceDataHWater>() {
        @Override
        public Comparator<ContServiceDataHWater> getDataComparator() {
            return Comparator.comparing(ContServiceDataHWater::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    };


    private ConsumptionDataProcessorAbs<ContServiceDataElCons> elConsDataProcessor = new ConsumptionDataProcessorAbs<ContServiceDataElCons>() {
        @Override
        public Comparator<ContServiceDataElCons> getDataComparator() {
            return Comparator.comparing(ContServiceDataElCons::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    };



    /**
     *
     * @param task
     * @param inDataList
     * @param prePeriodLastDataLoader
     * @param contZPointIdGetter
     * @param consumptionDataProcessor
     * @param consumptionFunctionGetter
     * @param md5Hash
     * @param <T>
     * @return
     */
    private <T> List<ContZPointConsumption> processAnyListUniversal(final ConsumptionTask task,
                                                                    final List<T> inDataList,
                                                                    final Optional<Function<List<Long>, List<T>>> prePeriodLastDataLoader,
                                                                    Function<T, Long> contZPointIdGetter,
                                                                    ConsumptionDataProcessor<T> consumptionDataProcessor,
                                                                    Function<ContZPoint, List<ConsumptionFunction<T>>> consumptionFunctionGetter,
                                                                    boolean md5Hash) {

        Objects.requireNonNull(task);

        Optional<ContZPointConsumptionTask> optContZPointConsumptionTask = task.getTaskUUID() == null ? Optional.empty() :
            consumptionTaskRepository.findByTaskUUID(task.getTaskUUID()).stream().findAny();

        Boolean taskStateUpdateResult = updateTaskState (task, TASK_STATE_CALCULATING, TASK_STATE_SCHEDULED);
        if (Boolean.FALSE.equals(taskStateUpdateResult)) {
            throw new IllegalStateException("Expecting state of task is not " + TASK_STATE_SCHEDULED);
        }

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(ConsumptionDataTypeKey.builder().dataType(task.getDataType()).destTimeDetailType(task.getDestTimeDetailType()).consDateTime(task.getDateTimeFrom()).build());

        cleanConsumptionDataType(cleanKeys2);

        if (inDataList.isEmpty()) {
            updateTaskState (task, TASK_STATE_FINISHED);
            return Collections.emptyList();
        }

        final Map<Long, List<T>> periodDataMap = GroupUtil.makeIdMap(inDataList, contZPointIdGetter);

        log.trace("ContZPoints: ");
        periodDataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, periodDataMap.get(i).size()));

        final Optional<Long> optTaskId = optContZPointConsumptionTask.map(i -> i.getId());

        List<Long> contZPointIds = periodDataMap.keySet().stream().distinct().collect(Collectors.toList());
        List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);
        Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        prePeriodLastDataLoader.ifPresent(i -> log.debug("Pre period Data fetching"));
        Optional<List<T>> optPrePeriodLastDataDataAll = prePeriodLastDataLoader.map(i -> i.apply(contZPointIds));
        prePeriodLastDataLoader.ifPresent(i -> log.debug("Pre period Data fetching complete"));

        if (optPrePeriodLastDataDataAll.isPresent()) {
            if (optPrePeriodLastDataDataAll.get() == null ||
                optPrePeriodLastDataDataAll.get().isEmpty()) {
                updateTaskState (task, TASK_STATE_FINISHED);
                return Collections.emptyList();
            }
        }

        Optional<Map<Long, List<T>>> optPrePeriodLastDataDataMap = optPrePeriodLastDataDataAll
            .map(i -> GroupUtil.makeIdMap(optPrePeriodLastDataDataAll.get(), contZPointIdGetter));

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

            List<ConsumptionFunction<T>> consumptionFunctions = consumptionFunctionGetter.apply(contZPoint);

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

        updateTaskState (task, TASK_STATE_FINISHED);

        return consumptionDataList;
    }


}
