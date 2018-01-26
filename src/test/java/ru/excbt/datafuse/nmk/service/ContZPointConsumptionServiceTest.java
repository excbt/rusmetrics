package ru.excbt.datafuse.nmk.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.support.InstantPeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class ContZPointConsumptionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ContZPointConsumptionServiceTest.class);

    @Autowired
    private ContZPointConsumptionService contZPointConsumptionService;


    @Test
    public void testDTO() {


        LocalDateTimePeriod localDateTimePeriod = LocalDateTimePeriod.month(2017,2);

        InstantPeriod period = InstantPeriod.month(2017,2);

        log.info("InstantPeriod from {} to {}", period.getDateTimeFrom(), period.getDateTimeTo());
        log.info("LocalDatePeriod from {} to {}", localDateTimePeriod.getDateTimeFrom(), localDateTimePeriod.getDateTimeTo());

        List<ContZPointConsumptionDTO> consumptionList = contZPointConsumptionService.getConsumption(128551684L,
            TimeDetailKey.TYPE_24H,
            ConsumptionService.DataType.ELECTRICITY, localDateTimePeriod
            );

        assertNotNull(consumptionList);
        consumptionList.forEach(i -> {
            log.info("consDate:{} valueName:{}, value: {}", i.getConsDateTime(), i.getConsValueName(), i.getConsValue());
        });

    }
}
