package ru.excbt.datafuse.nmk.service;

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
import ru.excbt.datafuse.nmk.domain.datatype.ArrayUtil;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.handling.ConsumptionFunction;
import ru.excbt.datafuse.nmk.service.vm.ZPointConsumptionVM;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConsumptionService {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionService.class);

    public static final String MD5_HASH_SECRET = "YsoB66IIyYlEFw50ObB2";
    public static final byte[] MD5_HASH_SECRET_BYTES = MD5_HASH_SECRET.getBytes(Charset.forName("UTF8"));

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
    public void processHWater(ContZPoint contZPoint, TimeDetailKey timeDetailKey, LocalDateTimePeriod period, boolean md5Hash) {

        Objects.requireNonNull(contZPoint);
        Objects.requireNonNull(contZPoint.getId());
        Objects.requireNonNull(timeDetailKey);
        Objects.requireNonNull(period);

        if (period.isInvalid()) {
            throw new IllegalArgumentException("period is invalid");
        }

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
                                                                                contZPoint.getId(),
                                                                                timeDetailKey.getKeyname(),
                                                                                period.getFromDate(),
                                                                                period.getToDate());

        processHWaterList (period, data, md5Hash);

    }

    /**
     *
     * @param timeDetailKey
     * @param period
     */
    @Transactional
    public void processHWater(TimeDetailKey timeDetailKey, LocalDateTimePeriod period, boolean md5Hash) {

        List<ContServiceDataHWater> data = dataHWaterRepository.selectForConsumption(
            timeDetailKey.getKeyname(),
            period.getFromDate(),
            period.getToDate());

        processHWaterList (period, data, md5Hash);

    }

    @Transactional
    public void processHWater(ConsumptionTask task) {
        Objects.requireNonNull(task);

        TimeDetailKey timeDetailKey = TimeDetailKey.searchKeyname(task.getTimeDetailType());
        LocalDateTimePeriod period = LocalDateTimePeriod.builder().dateTimeFrom(task.getDateTimeFrom()).dateTimeTo(task.getDateTimeTo()).build();
        if (timeDetailKey != null && period.isValid()) {
            processHWater(timeDetailKey,period,true);
        } else {
            log.warn("ConsumptionTask is invalid: {}", task);
        }
    }


    private ConsumptionFunction<ContServiceDataHWater> cons_M1 = new ConsumptionFunction<>("M1", (d) -> d.getM_in());

    private ConsumptionFunction<ContServiceDataHWater> cons_M1_sub_M2 = new ConsumptionFunction<>("M1-M2", (d) -> Math.abs(d.getM_in() - d.getM_out()));

    /**
     *
     * @param period
     * @param dataHWaterList
     */
    private void processHWaterList(LocalDateTimePeriod period, List<ContServiceDataHWater> dataHWaterList, boolean md5Hash) {

        Set<ConsumptionCleanKey> cleanKeys = dataHWaterList.stream().map(i -> new ConsumptionCleanKey(i, period.getDateTimeFrom())).collect(Collectors.toSet());
        cleanConsumption(cleanKeys);

        Map<Long, List<ContServiceDataHWater>> dataMap = GroupUtil.makeIdMap(dataHWaterList, (i) -> i.getContZPointId());
        log.debug("ContZPoints: ");
        dataMap.keySet().stream().forEach(i -> log.debug("Id: {}, Size: {}", i, dataMap.get(i).size()));

        dataMap.keySet().stream().forEach(i -> {
            ContZPoint contZPoint = contZPointRepository.findOne(i);

            ContServiceDataHWater data = dataMap.get(i).get(0);

            List<ContServiceDataHWater> dataHWaters = dataMap.get(i);

            List<String> timeDetailTypes = dataHWaters.stream().map(ContServiceDataHWater::getTimeDetailType).distinct().collect(Collectors.toList());

            if (timeDetailTypes.size() != 1) {
                throw new IllegalStateException("Time detail type is invalid");
            }

            ConsumptionFunction<ContServiceDataHWater> consFunc = Boolean.TRUE.equals(contZPoint.getDoublePipe()) ?
                cons_M1_sub_M2 : cons_M1;

            double[] consValues = dataHWaters.stream().map(d -> consFunc.getFunc().apply(d)).mapToDouble(x -> x).toArray();

            ContZPointConsumption consumption = new ContZPointConsumption();
            consumption.setContServiceType(contZPoint.getContServiceTypeKeyname());
            consumption.setContZPointId(i);
            consumption.setTimeDetailType(timeDetailTypes.get(0));
            consumption.setDateFrom(period.getDateTimeFrom());
            consumption.setDateTo(period.getDateTimeTo());
            consumption.setConsFunc(consFunc.getFuncName());
            consumption.setConsData(consValues);

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


    private class ConsumptionCleanKey {
        private final Long contZPointId;
        private final String timeDetailType;
        private final LocalDateTime dateTimeFrom;

        private ConsumptionCleanKey (ContServiceDataHWater hWater, LocalDateTime dateTimeFrom) {
            this.contZPointId = hWater.getContZPointId();
            this.timeDetailType = hWater.getTimeDetailType();
            this.dateTimeFrom = dateTimeFrom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConsumptionCleanKey that = (ConsumptionCleanKey) o;
            return Objects.equals(contZPointId, that.contZPointId) &&
                Objects.equals(timeDetailType, that.timeDetailType) &&
                Objects.equals(dateTimeFrom, that.dateTimeFrom);
        }

        @Override
        public int hashCode() {

            return Objects.hash(contZPointId, timeDetailType, dateTimeFrom);
        }
    }

    private void cleanConsumption(Collection<ConsumptionCleanKey> cleanKeys) {
        cleanKeys.stream().forEach(key -> {
            consumptionRepository.deleteByKey(key.contZPointId, key.timeDetailType, key.dateTimeFrom);
        });

    }

}
