package ru.excbt.datafuse.nmk.service;

import lombok.Builder;
import lombok.Getter;
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
import java.util.stream.Collectors;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    public static final String MD5_HASH_SECRET = "YsoB66IIyYlEFw50ObB2";
    public static final byte[] MD5_HASH_SECRET_BYTES = MD5_HASH_SECRET.getBytes(Charset.forName("UTF8"));

    public static final String CONS_STATUS_CALCULATED = "CALCULATED";
    public static final String CONS_STATUS_INVALIDATED = "INVALIDATED";

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

        TimeDetailKey srcTimeDetailKey = TimeDetailKey.searchKeyname(task.getSrcTimeDetailType());
        LocalDateTimePeriod period = LocalDateTimePeriod.builder().dateTimeFrom(task.getDateTimeFrom()).dateTimeTo(task.getDateTimeTo()).build();

        if (srcTimeDetailKey != null && period.isValid()) {

            List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
                srcTimeDetailKey.getKeyname(),
                period.getFromDate(),
                period.getToDate());

            processHWaterList (task, data, true);

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

        Set<ConsumptionZPointKey> cleanKeys = dataHWaterList.stream()
            .map(i -> new ConsumptionZPointKey(i.getContZPointId(), task.getDestTimeDetailType(), task.getDateTimeFrom()))
            .collect(Collectors.toSet());

        cleanConsumption(cleanKeys);

        Map<Long, List<ContServiceDataHWater>> dataMap = GroupUtil.makeIdMap(dataHWaterList, (i) -> i.getContZPointId());
        log.debug("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.debug("Id: {}, Size: {}", i, dataMap.get(i).size()));

        dataMap.keySet().stream().forEach(i -> {
            ContZPoint contZPoint = contZPointRepository.findOne(i);

            ContServiceDataHWater data = dataMap.get(i).get(0);

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

            ContZPointConsumption consumption = new ContZPointConsumption();
            consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
            consumption.setContZPointId(i);
            consumption.setSrcTimeDetailType(srcTimeDetailType);
            consumption.setDestTimeDetailType(task.getDestTimeDetailType());
            consumption.setDateFrom(task.getDateTimeFrom());
            consumption.setDateTo(task.getDateTimeTo());
            consumption.setConsFunc(consFunc.getFuncName());
            consumption.setConsData(consValues);
            consumption.setMeasureUnit(consFunc.getMeasureUnit());
            consumption.setStatus(CONS_STATUS_CALCULATED);

            if (md5Hash) {
                String hash = calcMd5Hash(consValues);
                consumption.setConsDataMD5(hash);
            }

            consumptionRepository.save(consumption);
            log.debug("Consumption ID: {}, Hash: {}, MD5: {}", consumption.getId(), consValues.hashCode(), consumption.getConsDataMD5());
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
    public static class ConsumptionZPointKey {
        private final Long contZPointId;
        private final String destTimeDetailType;
        private final LocalDateTime dateTimeFrom;

        private ConsumptionZPointKey (Long contZPointId, String destTimeDetailType, LocalDateTime dateTimeFrom) {
            this.contZPointId = contZPointId;
            this.destTimeDetailType = destTimeDetailType;
            this.dateTimeFrom = dateTimeFrom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConsumptionZPointKey key = (ConsumptionZPointKey) o;
            return Objects.equals(destTimeDetailType, key.destTimeDetailType) &&
                Objects.equals(dateTimeFrom, key.dateTimeFrom) &&
                Objects.equals(contZPointId, key.contZPointId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(destTimeDetailType, dateTimeFrom, contZPointId);
        }
    }



    private void cleanConsumption(Collection<ConsumptionZPointKey> cleanKeys) {
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByKey(key.getContZPointId(), key.getDestTimeDetailType(), key.getDateTimeFrom());
        });
    }

    @Transactional
    public void invalidateConsumption(Collection<ConsumptionZPointKey> cleanKeys) {
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.statusByKey(CONS_STATUS_INVALIDATED, key.getContZPointId(), key.getDestTimeDetailType(), key.getDateTimeFrom());
        });
    }

}
