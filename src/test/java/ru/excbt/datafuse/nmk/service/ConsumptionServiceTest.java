package ru.excbt.datafuse.nmk.service;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ConsumptionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionServiceTest.class);

    @Autowired
    private ConsumptionService consumptionService;

    @Autowired
    private ContZPointConsumptionRepository zPointConsumptionRepository;

    @Autowired
    private ContZPointRepository contZPointRepository;

    @Test
    @Transactional
    public void testEntity() {
        ContZPointConsumption consumption = new ContZPointConsumption();
        consumption.setContServiceType(ContServiceTypeKey.CW.getKeyname());
        zPointConsumptionRepository.saveAndFlush(consumption);
    }


    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterOne() {

        ContZPoint contZPoint = contZPointRepository.findOne(71843481L);

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dateTimeFrom(day)
            .dateTimeTo(day.plusDays(1).minusSeconds(1)).build();

        consumptionService.processHWater(task, true);
    }


    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterDay() {

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConsumptionTask task = ConsumptionTask.builder()
            .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dateTimeFrom(day)
            .dateTimeTo(day.plusDays(1).minusSeconds(1)).build();

        consumptionService.processHWater(task, true);

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    /**
     *
     */
    @Test
    //@Transactional
    public void processHWaterDayTask() {

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        consumptionService.processHWater(
            ConsumptionTask.builder()
                .name("MyName")
                .dateTimeFrom(day)
                .dateTimeTo(day.plusDays(1).minusSeconds(1))
                .contServiceType(ContServiceTypeKey.HW.getKeyname())
                .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
                .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
                .retryCnt(3).build()
        );

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    @Test
    public void invalidateConsumption() {
        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        ConsumptionService.ConsumptionZPointKey key = ConsumptionService.ConsumptionZPointKey
            .builder()
            .contZPointId(71843465L)
            .dateTimeFrom(day)
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .build();
        Set<ConsumptionService.ConsumptionZPointKey> keys = new HashSet<>();
        keys.add(key);
        consumptionService.invalidateConsumption(keys);
    }
}
