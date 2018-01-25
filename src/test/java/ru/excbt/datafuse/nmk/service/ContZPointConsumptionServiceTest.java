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

        InstantPeriod period = InstantPeriod.month(2017,2);

        log.info("Period from {} to {}", period.getDateTimeFrom(), period.getDateTimeTo());

        List<ContZPointConsumptionDTO> consumptionList = contZPointConsumptionService.getConsumption(128551684L,
            TimeDetailKey.TYPE_24H,
            ConsumptionService.DataType.ELECTRICITY, period
            );

        assertNotNull(consumptionList);
        consumptionList.forEach(i -> {
            log.info("consDate:{} valueName:{}, value: {}", i.getConsDateTime(), i.getConsValueName(), i.getConsValue());
        });

    }
}
