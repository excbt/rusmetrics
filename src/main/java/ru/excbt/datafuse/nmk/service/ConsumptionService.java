package ru.excbt.datafuse.nmk.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    public static final String MD5_HASH_SECRET = "YsoB66IIyYlEFw50ObB2";
    public static final byte[] MD5_HASH_SECRET_BYTES = MD5_HASH_SECRET.getBytes(Charset.forName("UTF8"));

    public static final String CONS_STATUS_CALCULATED = "CALCULATED";
    public static final String CONS_STATUS_INVALIDATED = "INVALIDATED";

    public static final String DATA_TYPE_HWATER = "HWATER";

    private final ContServiceDataHWaterRepository dataHWaterRepository;

    private final ContServiceDataImpulseRepository dataImpulseRepository;

    private final ContServiceDataElConsRepository dataElConsRepository;

    private final ContZPointConsumptionRepository consumptionRepository;

    private final ContZPointRepository contZPointRepository;

    @Autowired
    public ConsumptionService(ContServiceDataHWaterRepository dataHWaterRepository, ContServiceDataImpulseRepository dataImpulseRepository, ContServiceDataElConsRepository dataElConsRepository, ContZPointConsumptionRepository consumptionRepository, ContZPointRepository contZPointRepository) {
        this.dataHWaterRepository = dataHWaterRepository;
        this.dataImpulseRepository = dataImpulseRepository;
        this.dataElConsRepository = dataElConsRepository;
        this.consumptionRepository = consumptionRepository;
        this.contZPointRepository = contZPointRepository;
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
    public void processHWater(ContZPoint contZPoint, ConsumptionTask task, boolean md5Hash) {

        Objects.requireNonNull(contZPoint);
        Objects.requireNonNull(contZPoint.getId());
        Objects.requireNonNull(task);

        DateInterval period = task.toDateInterval();

        if (period.isInvalid()) {
            throw new IllegalArgumentException("period is invalid");
        }

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
                                                                                contZPoint.getId(),
            task.getSrcTimeDetailType(),
            period.getFromDate(),
            period.getToDate());

        processHWaterList (task, data, md5Hash);

    }

    /**
     *
     * @param task
     * @param md5Hash
     */
    @Transactional
    public void processHWater(ConsumptionTask task, boolean md5Hash) {

        DateInterval period = task.toDateInterval();

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
            task.getSrcTimeDetailType(),
            period.getFromDate(),
            period.getToDate());

        processHWaterList (task, data, md5Hash);

    }

    @Transactional
    public void processHWater(ConsumptionTask task) {
        Objects.requireNonNull(task);

        if (!task.isValid()) {
            log.warn("Task is invalid");
            return;
        }

        log.debug("Processing task: {}" ,task.toString());

        TimeDetailKey srcTimeDetailKey = TimeDetailKey.searchKeyname(task.getSrcTimeDetailType());
        LocalDateTimePeriod period = LocalDateTimePeriod.builder().dateTimeFrom(task.getDateTimeFrom()).dateTimeTo(task.getDateTimeTo()).build();

        if (srcTimeDetailKey != null && period.isValid()) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
                srcTimeDetailKey.getKeyname(),
                period.getFromDate(),
                period.getToDate());
            stopWatch.stop();
            log.debug("task data loaded in {}", stopWatch.toString());
            stopWatch.reset();
            stopWatch.start();
            processHWaterList (task, data, true);
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
    private void processHWaterList(ConsumptionTask task, List<ContServiceDataHWater> dataHWaterList, boolean md5Hash) {

        Set<ConsumptionDataTypeKey> cleanKeys2 = new HashSet<>();
        cleanKeys2.add(ConsumptionDataTypeKey.builder().dataType(DATA_TYPE_HWATER).destTimeDetailType(task.getDestTimeDetailType()).consDateTime(task.getDateTimeFrom()).build());

        cleanConsumptionDataType(cleanKeys2);

//        Set<ConsumptionZPointKey> cleanKeys = dataHWaterList.stream()
//            .map(i -> new ConsumptionZPointKey(i.getContZPointId(), task.getDestTimeDetailType(), task.getDateTimeFrom()))
//            .collect(Collectors.toSet());
//
//        log.debug("Clean consumption in progress");
//        cleanConsumptionZPoint(cleanKeys);
//        log.debug("Clean consumption complete");

        Map<Long, List<ContServiceDataHWater>> dataMap = GroupUtil.makeIdMap(dataHWaterList, (i) -> i.getContZPointId());
        log.trace("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.trace("Id: {}, Size: {}", i, dataMap.get(i).size()));

        List<Long> contZPointIds = dataMap.keySet().stream().distinct().collect(Collectors.toList());

        //List<ContZPoint> contZPoints = contZPointRepository.findByIds(contZPointIds);

        //Map<Long, ContZPoint> contZPointMap = contZPoints.stream().collect(Collectors.toMap(x -> x.getId(), Function.identity()));

        dataMap.keySet().stream().forEach(i -> {
            ContZPoint contZPoint = contZPointRepository.findOne(i);
            //contZPointMap.get(i);

            if (contZPoint == null) {
                log.warn("ContZPoint (id={}) is not found", i);
                return;
            }
            //contZPointRepository.findOne(i);

            //ContServiceDataHWater data = dataMap.get(i).get(0);

            List<ContServiceDataHWater> dataHWaters = dataMap.get(i);

            Set<String> srcTimeDetailTypes = dataHWaters.stream().map(ContServiceDataHWater::getTimeDetailType).distinct().collect(Collectors.toSet());

            if (srcTimeDetailTypes.size() != 1) {
                throw new IllegalStateException("Time detail type is invalid");
            }

            String srcTimeDetailType = srcTimeDetailTypes.iterator().next();

            ConsumptionFunction<ContServiceDataHWater> consFunc = ConsumptionFunctionLib.findHWaterFunc(contZPoint);

            double[] consValues = dataHWaters.stream()
                .filter(d -> consFunc.getFilter().test(d))
                .map(d -> consFunc.getFunc().apply(d))
                .mapToDouble(x -> x).toArray();

            if (consValues.length == 0) {
                return;
            }

            ContZPointConsumption consumption = new ContZPointConsumption();
            consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
            consumption.setContZPointId(i);
            consumption.setDataType(DATA_TYPE_HWATER);
            consumption.setConsDateTime(task.getDateTimeFrom());
            consumption.setSrcTimeDetailType(srcTimeDetailType);
            consumption.setDestTimeDetailType(task.getDestTimeDetailType());
            consumption.setDateTimeFrom(task.getDateTimeFrom());
            consumption.setDateTimeTo(task.getDateTimeTo());
            consumption.setConsFunc(consFunc.getFuncName());
            consumption.setConsData(consValues);
            consumption.setMeasureUnit(consFunc.getMeasureUnit());
            consumption.setStatus(CONS_STATUS_CALCULATED);

            if (md5Hash) {
                String hash = calcMd5Hash(consValues);
                consumption.setConsDataMD5(hash);
            }

            consumptionRepository.save(consumption);
            log.trace("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consValues.hashCode(), consumption.getConsDataMD5());
        });

        consumptionRepository.flush();
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
     */
    @Getter
    @Builder
    @EqualsAndHashCode
    public static class ConsumptionZPointKey {
        private final Long contZPointId;
        private final String destTimeDetailType;
        private final LocalDateTime contDateTime;
    }


    @Getter
    @Builder
    @EqualsAndHashCode
    public static class ConsumptionDataTypeKey {
        private final String dataType;
        private final String destTimeDetailType;
        private final LocalDateTime consDateTime;
    }


    private void cleanConsumptionZPoint(Collection<ConsumptionZPointKey> cleanKeys) {
        log.debug("Cleaning {} keys", cleanKeys.size());
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByKey(key.getContZPointId(), key.getDestTimeDetailType(), key.getContDateTime());
        });
    }

    @Transactional
    public void invalidateConsumption(Collection<ConsumptionZPointKey> cleanKeys) {
        log.debug("Updating {} keys", cleanKeys.size());
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.updateStatusByKey(CONS_STATUS_INVALIDATED, key.getContZPointId(), key.getDestTimeDetailType(), key.getContDateTime());
        });
    }

    private void cleanConsumptionDataType(Collection<ConsumptionDataTypeKey> cleanKeys) {
        log.debug("Cleaning {} keys", cleanKeys.size());
        log.debug("Clean consumption by dataType in progress");
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByDataTypeKey(key.getDataType(), key.getDestTimeDetailType(), key.getConsDateTime());
        });
        log.debug("Clean consumption by dataType complete");
    }


}
