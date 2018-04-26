package ru.excbt.datafuse.nmk.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ContZPointConsumptionServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(ContZPointConsumptionServiceTest.class);

    @Autowired
    private ContZPointConsumptionService contZPointConsumptionService;


    @Test
    public void testDTO() {


        LocalDateTimePeriod localDateTimePeriod = LocalDateTimePeriod.month(2017,2);
        LocalDateTimePeriod localDateTimePeriod2 = LocalDateTimePeriod.day(2017,2, 24);

        log.info("LocalDatePeriod from {} to {}", localDateTimePeriod.getFrom(), localDateTimePeriod.getTo());
        log.info("LocalDatePeriod valid {} from {} to {}", localDateTimePeriod2.isValidEq(), localDateTimePeriod2.getFrom(), localDateTimePeriod2.getTo());

        List<ContZPointConsumptionDTO> consumptionList = contZPointConsumptionService.getConsumptionData(128551684L,
            TimeDetailKey.TYPE_24H,
            ConsumptionService.DataType.ELECTRICITY, localDateTimePeriod
            );

        assertNotNull(consumptionList);
        consumptionList.forEach(i -> {
            log.info("consDate:{} valueName:{}, value: {}", i.getConsDateTime(), i.getConsValueName(), i.getConsValue());
        });

    }


    @Test
    public void testGetDataType() {
        Optional<ConsumptionService.DataType> contZPointOptional = contZPointConsumptionService.findDataType(129832662L);
        assertNotNull(contZPointOptional);
        log.info("DataType: {}", contZPointOptional.isPresent());
        contZPointOptional.ifPresent(i -> {
            log.info("DataType: {}", i);
        });

    }
}
