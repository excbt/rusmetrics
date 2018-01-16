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

        //final ConsumptionTask workTask = task.newIfNoUUID();
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

        processHWaterList (workTask, data, md5Hash);

    }

    /**
     *
     * @param task
     * @param md5Hash
     */
    @Transactional
    public void processHWater(final ConsumptionTask task, boolean md5Hash) {

        Objects.requireNonNull(task);

        //ConsumptionTask workTask = task.newIfNoUUID();
        ConsumptionTask workTask = task;

        DateInterval period = workTask.toDateInterval();

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumptionAny(
            workTask.getSrcTimeDetailType(),
            period.getFromDate(),
            period.getToDate(),
            workTask.getContZPointId());

        processHWaterList (workTask, data, md5Hash);

    }

    @Transactional
    public void processHWater(final ConsumptionTask task) {
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

            processAnyList(task,
                dataHWaterList,
                d -> d.getContZPointId(),
                Comparator.comparing(ContServiceDataHWater::getDataDate, Comparator.nullsLast(Comparator.naturalOrder())),
                zp -> ConsumptionFunctionLib.findHWaterFunc(zp),
                true
            );


//            processHWaterList (task, dataHWaterList, true);

            stopWatch.stop();
            log.debug("task data processed in {}", stopWatch.toString());
        } else {
            log.warn("ConsumptionTask is invalid: {}", task);
        }

    }



    /**
     *
     * @param task
     * @param dataHWaterList
     */
    private void processHWaterList(final ConsumptionTask task, List<ContServiceDataHWater> dataHWaterList, boolean md5Hash) {

        Objects.requireNonNull(task);

        Optional<ContZPointConsumptionTask> optContZPointConsumptionTask = task.getTaskUUID() == null ? Optional.empty() :
            consumptionTaskRepository.findByTaskUUID(task.getTaskUUID()).stream().findAny();

        optContZPointConsumptionTask = optContZPointConsumptionTask.map(i -> {

            if (!TASK_STATE_SCHEDULED.equals(i.getTaskState())) {
                throw new IllegalStateException("Illegal task status");
            }

            i.setTaskState(TASK_STATE_CALCULATING);
            i.setTaskDateTime(Instant.now());
            return consumptionTaskRepository.saveAndFlush(i);
        });

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(ConsumptionDataTypeKey.builder().dataType(DATA_TYPE_HWATER).destTimeDetailType(task.getDestTimeDetailType()).consDateTime(task.getDateTimeFrom()).build());

        cleanConsumptionDataType(cleanKeys2);

//        Set<ConsumptionZPointKey> cleanKeys = dataHWaterList.stream()
//            .map(i -> new ConsumptionZPointKey(i.getContZPointId(), workTask.getDestTimeDetailType(), workTask.getDateTimeFrom()))
//            .collect(Collectors.toSet());
//
//        cleanConsumptionZPoint(cleanKeys);

        Map<Long, List<ContServiceDataHWater>> dataMap = GroupUtil.makeIdMap(dataHWaterList, (i) -> i.getContZPointId());
        log.trace("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, dataMap.get(i).size()));

        //List<Long> contZPointIds = dataMap.keySet().stream().distinct().collect(Collectors.toList());
        //List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);
        //Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        final Long taskId = optContZPointConsumptionTask.map(i -> i.getId()).orElse(null);

        Comparator<ContServiceDataHWater> cmpByDataDate = Comparator.comparing(ContServiceDataHWater::getDataDate, Comparator.nullsLast(Comparator.naturalOrder()));

        dataMap.keySet().stream().sorted().forEach(i -> {
            ContZPoint contZPoint = contZPointRepository.findOne(i);
            //contZPointMap.get(i);

            if (contZPoint == null) {
                log.warn("ContZPoint (id={}) is not found", i);
                return;
            }

            final List<ContServiceDataHWater> dataHWaters = dataMap.get(i);

            Set<String> srcTimeDetailTypes = dataHWaters.stream().map(ContServiceDataHWater::getTimeDetailType).distinct().collect(Collectors.toSet());

            if (srcTimeDetailTypes.size() != 1) {
                throw new IllegalStateException("Time detail type is invalid");
            }

            List<ConsumptionFunction<ContServiceDataHWater>> consumptionFunctions = ConsumptionFunctionLib.findHWaterFunc(contZPoint);

            List<ContZPointConsumption> consumptions = new ArrayList<>();

            for (ConsumptionFunction<ContServiceDataHWater> consFunc: consumptionFunctions) {
                double[] consValues = ConsumptionFunctionLib.allValues(dataHWaters, cmpByDataDate, consFunc);

                if (consValues.length == 0) {
                    continue;
                }

                ContZPointConsumption consumption = new ContZPointConsumption();
                consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
                consumption.setContZPointId(i);
                consumption.setDataType(DATA_TYPE_HWATER);
                consumption.setConsDateTime(task.getDateTimeFrom());
                consumption.setSrcTimeDetailType(task.getSrcTimeDetailType());
                consumption.setDestTimeDetailType(task.getDestTimeDetailType());
                consumption.setDateTimeFrom(task.getDateTimeFrom());
                consumption.setDateTimeTo(task.getDateTimeTo());
                consumption.setConsFunc(consFunc.getFuncName());
                consumption.setConsData(consValues);
                consumption.setMeasureUnit(consFunc.getMeasureUnit());
                consumption.setConsState(CONS_STATE_CALCULATED);
                consumption.setConsTaskId(taskId);

                if (md5Hash) {
                    String hash = calcMd5Hash(consValues);
                    consumption.setConsMD5(hash);
                }
                log.trace("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consValues.hashCode(), consumption.getConsMD5());
                consumptions.add(consumption);
            }
            consumptionRepository.save(consumptions);
        });

        consumptionRepository.flush();

        optContZPointConsumptionTask = optContZPointConsumptionTask.map( i -> {
                i.setTaskState(TASK_STATE_FINISHED);
                i.setTaskDateTime(Instant.now());
                return consumptionTaskRepository.saveAndFlush(i);
            }
        );

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

            processAnyListAggr(task,
                            data,
                            prePeriodLastDataLoader,
                            i -> i.getContZPointId(),
                            Comparator.comparing(ContServiceDataElCons::getDataDate, Comparator.nullsLast(Comparator.naturalOrder())),
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
     * @param task
     * @param inDataList
     * @param md5Hash
     */
    private <T> void processAnyList(final ConsumptionTask task,
                                    final List<T> inDataList,
                                    Function<T, Long> contZPointIdGetter,
                                    Comparator<T> cmp,
                                    Function<ContZPoint, List<ConsumptionFunction<T>>> consumptionFunctionGetter,
                                    boolean md5Hash) {

        Objects.requireNonNull(task);

        Optional<ContZPointConsumptionTask> optContZPointConsumptionTask = task.getTaskUUID() == null ? Optional.empty() :
            consumptionTaskRepository.findByTaskUUID(task.getTaskUUID()).stream().findAny();

        optContZPointConsumptionTask = optContZPointConsumptionTask.map(i -> {

            if (!TASK_STATE_SCHEDULED.equals(i.getTaskState())) {
                throw new IllegalStateException("Illegal task status");
            }

            i.setTaskState(TASK_STATE_CALCULATING);
            i.setTaskDateTime(Instant.now());
            return consumptionTaskRepository.saveAndFlush(i);
        });

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(ConsumptionDataTypeKey.builder().dataType(task.getDataType()).destTimeDetailType(task.getDestTimeDetailType()).consDateTime(task.getDateTimeFrom()).build());
        cleanConsumptionDataType(cleanKeys2);

        Map<Long, List<T>> dataMap = GroupUtil.makeIdMap(inDataList, contZPointIdGetter);
        log.trace("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, dataMap.get(i).size()));

        final Long taskId = optContZPointConsumptionTask.map(i -> i.getId()).orElse(null);

        List<Long> contZPointIds = dataMap.keySet().stream().distinct().collect(Collectors.toList());
        List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);
        Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        log.debug("Starting loop for: {} keys", dataMap.keySet().size());

        dataMap.keySet().stream().sorted().forEach(i -> {
            final ContZPoint contZPoint = contZPointMap.get(i);

            final List<T> contZPointData = dataMap.get(i);

            if (contZPoint == null) {
                log.warn("ContZPoint (id={}) is not found", i);
                return;
            }
            List<ConsumptionFunction<T>> consumptionFunctions = consumptionFunctionGetter.apply(contZPoint);

            List<ContZPointConsumption> consumptionDataList = new ArrayList<>();

            for (ConsumptionFunction<T> consFunc: consumptionFunctions) {
                double[] consValues = ConsumptionFunctionLib.allValues(contZPointData, cmp, consFunc);

                if (consValues.length == 0) {
                    continue;
                }

                ContZPointConsumption consumption = new ContZPointConsumption();
                consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
                consumption.setContZPointId(i);
                consumption.setDataType(task.getDataType());
                consumption.setConsDateTime(task.getDateTimeFrom());
                consumption.setSrcTimeDetailType(task.getSrcTimeDetailType());
                consumption.setDestTimeDetailType(task.getDestTimeDetailType());
                consumption.setDateTimeFrom(task.getDateTimeFrom());
                consumption.setDateTimeTo(task.getDateTimeTo());
                consumption.setConsFunc(consFunc.getFuncName());
                consumption.setConsData(consValues);
                consumption.setMeasureUnit(consFunc.getMeasureUnit());
                consumption.setConsState(CONS_STATE_CALCULATED);
                consumption.setConsTaskId(taskId);

                if (md5Hash) {
                    //String hash = calcMd5Hash(consValues);
                    String hash = calcMd5Hash(consumption);
                    consumption.setConsMD5(hash);
                }
                log.trace("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consValues.hashCode(), consumption.getConsMD5());
                consumptionDataList.add(consumption);
            }
            consumptionRepository.save(consumptionDataList);
        });

        log.debug("End loop for: {} keys", dataMap.keySet().size());

        consumptionRepository.flush();

        optContZPointConsumptionTask.ifPresent( i -> {
                i.setTaskState(TASK_STATE_FINISHED);
                i.setTaskDateTime(Instant.now());
                consumptionTaskRepository.saveAndFlush(i);
            }
        );

    }


    private MessageDigest md5Hash() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String calcMd5Hash (double[] data) {
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
    public boolean updateState(final ConsumptionTask consumptionTask, String taskState) {
        if (consumptionTask.getTaskUUID() == null) {
            return false;
        }

        Optional<ContZPointConsumptionTask> task = consumptionTaskRepository.findByTaskUUID(consumptionTask.getTaskUUID()).stream().findFirst();
        task.ifPresent( i -> {
                i.setTaskState(taskState);
                i.setTaskStateDt(Instant.now());
                consumptionTaskRepository.save(i);
            }
        );

        return task.isPresent();
    }


    /**
     *
     * @param task
     * @param inDataList
     * @param prePeriodLastDataLoader
     * @param contZPointIdGetter
     * @param dataComparator
     * @param consumptionFunctionGetter
     * @param md5Hash
     * @param <T>
     */
    private <T> List<ContZPointConsumption> processAnyListAggr( final ConsumptionTask task,
                                                                final List<T> inDataList,
                                                                final Function<List<Long>, List<T>> prePeriodLastDataLoader,
                                                                Function<T, Long> contZPointIdGetter,
                                                                Comparator<T> dataComparator,
                                                                Function<ContZPoint, List<ConsumptionFunction<T>>> consumptionFunctionGetter,
                                                                boolean md5Hash) {

        Objects.requireNonNull(task);

        Optional<ContZPointConsumptionTask> optContZPointConsumptionTask = task.getTaskUUID() == null ? Optional.empty() :
            consumptionTaskRepository.findByTaskUUID(task.getTaskUUID()).stream().findAny();

        optContZPointConsumptionTask = optContZPointConsumptionTask.map(i -> {

            if (!TASK_STATE_SCHEDULED.equals(i.getTaskState())) {
                throw new IllegalStateException("Illegal task status");
            }

            i.setTaskState(TASK_STATE_CALCULATING);
            i.setTaskDateTime(Instant.now());
            return consumptionTaskRepository.saveAndFlush(i);
        });

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(ConsumptionDataTypeKey.builder().dataType(task.getDataType()).destTimeDetailType(task.getDestTimeDetailType()).consDateTime(task.getDateTimeFrom()).build());

        cleanConsumptionDataType(cleanKeys2);

        final Map<Long, List<T>> periodDataMap = GroupUtil.makeIdMap(inDataList, contZPointIdGetter);

        log.trace("ContZPoints: ");
        periodDataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, periodDataMap.get(i).size()));

        final Long taskId = optContZPointConsumptionTask.map(i -> i.getId()).orElse(null);

        List<Long> contZPointIds = periodDataMap.keySet().stream().distinct().collect(Collectors.toList());
        List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);
        Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        log.debug("Pre period Data fetching");
        final List<T> prePeriodLastDataDataAll = prePeriodLastDataLoader.apply(contZPointIds);
        log.debug("Pre period Data fetching complete");

        Map<Long, List<T>> prePeriodLastDataDataMap = GroupUtil.makeIdMap(prePeriodLastDataDataAll, contZPointIdGetter);

        log.debug("Starting loop for: {} keys", periodDataMap.keySet().size());

        // For each ContZPoint

        List<ContZPointConsumption> consumptionDataList = new ArrayList<>();

        periodDataMap.keySet().stream().sorted().forEach(id -> {
            ContZPoint contZPoint = contZPointMap.get(id);
            if (contZPoint == null) {
                log.warn("ContZPoint (id={}) is not found", id);
                return;
            }
            List<T> periodData = periodDataMap.get(id);
            List<T> prePeriodLastData = prePeriodLastDataDataMap.get(id);

            List<ConsumptionFunction<T>> consumptionFunctions = consumptionFunctionGetter.apply(contZPoint);

            for (ConsumptionFunction<T> consFunc: consumptionFunctions) {
                Double periodLastValue = ConsumptionFunctionLib.lastValue(periodData, dataComparator, consFunc);
                if (periodLastValue == null) {
                    continue;
                }
                Double prePeriodLastValue = ConsumptionFunctionLib.lastValue(prePeriodLastData, dataComparator, consFunc);
                double[] consValues = new double[2];
                consValues[0] = periodLastValue != null ? periodLastValue : 0;
                consValues[1] = prePeriodLastValue != null ? prePeriodLastValue : 0;

                ContZPointConsumption consumption = new ContZPointConsumption();
                consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
                consumption.setContZPointId(id);
                consumption.setDataType(task.getDataType());
                consumption.setConsDateTime(task.getDateTimeFrom());
                consumption.setSrcTimeDetailType(task.getSrcTimeDetailType());
                consumption.setDestTimeDetailType(task.getDestTimeDetailType());
                consumption.setDateTimeFrom(task.getDateTimeFrom());
                consumption.setDateTimeTo(task.getDateTimeTo());
                consumption.setConsFunc(consFunc.getFuncName());
                consumption.setDataInAbs(prePeriodLastValue);
                consumption.setDataOutAbs(periodLastValue);
                consumption.setConsData(consValues);
                consumption.setMeasureUnit(consFunc.getMeasureUnit());
                consumption.setConsState(CONS_STATE_CALCULATED);
                consumption.setConsTaskId(taskId);

                if (md5Hash) {
                    //String hash = calcMd5Hash(consValues);
                    String hash = calcMd5Hash(consumption);
                    consumption.setConsMD5(hash);
                }
                log.trace("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consValues.hashCode(), consumption.getConsMD5());
                consumptionDataList.add(consumption);
            }
        });

        log.debug("End loop for: {} keys", periodDataMap.keySet().size());

        consumptionRepository.save(consumptionDataList);
        consumptionRepository.flush();

        optContZPointConsumptionTask.ifPresent( i -> {
                i.setTaskState(TASK_STATE_FINISHED);
                i.setTaskDateTime(Instant.now());
                consumptionTaskRepository.saveAndFlush(i);
            }
        );

        return consumptionDataList;
    }

}
