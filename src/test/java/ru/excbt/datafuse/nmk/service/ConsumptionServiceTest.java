package ru.excbt.datafuse.nmk.service;

import com.fasterxml.uuid.Generators;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;

import java.time.LocalDate;

import static ru.excbt.datafuse.nmk.service.consumption.ConsumptionTaskTemplate.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class ConsumptionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionServiceTest.class);

    public static final String TASK_STATE_SCHEDULED = ConsumptionService.TaskState.SCHEDULED.getKeyname();

    @Autowired
    private ConsumptionService consumptionService;

    @Autowired
    private ContZPointConsumptionRepository zPointConsumptionRepository;

    @Autowired
    private ContZPointRepository contZPointRepository;

    @Autowired
    private QueryDSLService queryDSLService;

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

        LocalDate day = LocalDate.of(2017, 5, 26);

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .template(Template24H_from_1H)
            .dateFrom(day)
            .dateTo(day).build().checkDaysBetween(1);

        consumptionService.processHWater(task, true);
    }


    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterDay() {

        LocalDate day = LocalDate.of(2017, 5, 26);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConsumptionTask task = ConsumptionTask.builder()
            .template(Template24H_from_1H)
            .dateFrom(day)
            .dateTo(day)
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .build().checkDaysBetween(1);

        consumptionService.processHWater(task, true);

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterDayTask() {

        LocalDate day = LocalDate.of(2017, 5, 26);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConsumptionTask task = ConsumptionTask.builder()
            .name("MyName")
            .dateFrom(day)
            .dateTo(day)
            .template(Template24H_from_1H)
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .retryCnt(3).build().checkDaysBetween(1);

        task = consumptionService.saveConsumptionTask(task, TASK_STATE_SCHEDULED);

        consumptionService.processHWater(task);

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    @Test
    @Transactional
    public void invalidateConsumption() {
        LocalDate day = LocalDate.of(2017, 5, 26);

        ConsumptionService.ConsumptionZPointKey key =
            new ConsumptionService.ConsumptionZPointKey(71843465L, TimeDetailKey.TYPE_24H.getKeyname(), day);

        consumptionService.invalidateConsumption(key);
    }


    @Test
    public void processHWaterConsumption_YYYY() {

        LocalDate startDay = LocalDate.of(2016, 1, 1);
        LocalDate endDay = LocalDate.of(2018,1,1);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //LocalDate day = startDay;

        ConsumptionTask task = ConsumptionTask.builder()
            .name("MyName")
            .dateFrom(startDay)
            .dateTo(startDay)
            .template(Template24H_from_1H)
            .taskUUID(Generators.timeBasedGenerator().generate())
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .retryCnt(3).build().checkDaysBetween(1);

        while (task.getDateFrom().isBefore(endDay) || task.getDateFrom().isEqual(endDay)) {
            log.info("Processing: {}-{}-{}", task.getDateFrom().getYear(), task.getDateFrom().getMonthValue(), task.getDateFrom().getDayOfMonth());
            ConsumptionTask savedTask = consumptionService.saveConsumptionTask(task, ConsumptionService.TaskState.SCHEDULED.getKeyname());
            consumptionService.processHWater(savedTask);
            task = savedTask.nextDay();
        }

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }


    @Test
    @Transactional
    public void processElOne() {

        ContZPoint contZPoint = contZPointRepository.findOne(128551676L);

        LocalDate day = LocalDate.of(2016, 10, 14);

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .dataType(ConsumptionService.DATA_TYPE_ELECTRICITY)
            .template(Template24H_from_1H)
            .dateFrom(day)
            .dateTo(day).build();

        consumptionService.processElCons(task.checkDaysBetween(1));

    }

    @Test
    //@Ignore
    public void processElConsumption_YYYY() {

        LocalDate startDay = LocalDate.of(2016, 1, 1);
        LocalDate endDay = LocalDate.of(2018,1,1);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        LocalDate day = startDay;
        while (day.isBefore(endDay) ) {
            log.info("Processing: {}-{}-{}", day.getYear(), day.getMonthValue(), day.getDayOfMonth());

            ConsumptionTask task = ConsumptionTask.builder()
                .name("MyName")
                .dataType(ConsumptionService.DATA_TYPE_ELECTRICITY)
                .template(Template24H_from_1H_ABS)
                .taskUUID(Generators.timeBasedGenerator().generate())
                .dateFrom(day)
                .dateTo(day)
                .build();

            task = consumptionService.saveConsumptionTask(task, TASK_STATE_SCHEDULED);

            consumptionService.processElCons(task.checkDaysBetween(1));

            day = day.plusDays(1);
        }

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }


    @Test
    @Transactional
    public void processImpulseOne() {

        ContZPoint contZPoint = contZPointRepository.findOne(128794022L);

        LocalDate day = LocalDate.of(2016, 12, 2);
        LocalDate endDay = day;

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .dataType(ConsumptionService.DATA_TYPE_IMPULSE)
            .template(Template24H_from_24H_ABS)
            .dateFrom(day)
            .dateTo(endDay).build().checkDaysBetween(1);

        consumptionService.processImpulse(task);


    }


    @Test
    //@Ignore
    public void processImpulseConsumption_YYYY() {

        LocalDate startDay = LocalDate.of(2016, 1, 1);
        LocalDate endDay = LocalDate.of(2018,1,1);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        LocalDate day = startDay;
        while (day.isBefore(endDay) ) {
            log.info("Processing: {}-{}-{}", day.getYear(), day.getMonthValue(), day.getDayOfMonth());

            ConsumptionTask task = ConsumptionTask.builder()
                .name("MyName")
                .dataType(ConsumptionService.DATA_TYPE_IMPULSE)
                .template(Template24H_from_24H_ABS)
                .taskUUID(Generators.timeBasedGenerator().generate())
                .dateFrom(day)
                .dateTo(day)
                .build().checkDaysBetween(1);

            task = consumptionService.saveConsumptionTask(task, TASK_STATE_SCHEDULED);

            consumptionService.processImpulse(task);

            day = day.plusDays(1);
        }

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }


}
