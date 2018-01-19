package ru.excbt.datafuse.nmk.service.consumption;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.service.ConsumptionService;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static ru.excbt.datafuse.nmk.service.consumption.ConsumptionTaskTemplate.Template24H_from_1H;
import static org.assertj.core.api.Assertions.assertThat;

public class ConsumptionTaskTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskTest.class);

    @Test
    public void testDaysDuration1() {

        LocalDate day = LocalDate.of(2017, 5, 26);

        ConsumptionTask task = ConsumptionTask.builder()
            .template(Template24H_from_1H)
            .dateFrom(day)
            .dateTo(day)
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .build();


        log.info("Days duration: {}", task.getDaysBetween());

        assertThat(task.getDaysBetween()).isEqualTo(1);
    }


    @Test
    public void testDaysDuration2() {

        LocalDate day = LocalDate.of(2017, 5, 26);

        ConsumptionTask task = ConsumptionTask.builder()
            .template(Template24H_from_1H)
            .dateFrom(day)
            .dateTo(day.plusDays(1))
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .build();


        log.info("Days duration: {}", task.getDaysBetween());

        assertThat(task.getDaysBetween()).isEqualTo(2);
    }


    @Test
    public void testNextDay() {
        ConsumptionTask task = ConsumptionTask.dayBuilder(LocalDate.now())
            .template(Template24H_from_1H)
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .build();

        ConsumptionTask nextDayTask = task.nextDay();

        assertThat(nextDayTask.getDaysBetween()).isEqualTo(1);

        assertThat(nextDayTask.getDateFrom()).isEqualTo(task.getDateFrom().plusDays(1));
    }

    @Test
    public void testNextContZPointId() {
        ConsumptionTask task = ConsumptionTask.dayBuilder(LocalDate.now())
            .template(Template24H_from_1H)
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .contZPointId(1L)
            .build();


        ConsumptionTask nextContZPointTask = task.nextContZPointId(2L);

        assertThat(nextContZPointTask.getContZPointId()).isEqualTo(2L);
    }

    }
