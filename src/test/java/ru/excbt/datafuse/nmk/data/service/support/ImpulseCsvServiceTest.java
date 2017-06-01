package ru.excbt.datafuse.nmk.data.service.support;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulseUCsv;
import ru.excbt.datafuse.nmk.data.service.ImpulseCsvService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kovtonyk on 31.05.2017.
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ImpulseCsvServiceTest extends JpaConfigTest {

    private static final Logger log = LoggerFactory.getLogger(ImpulseCsvServiceTest.class);

    @Autowired
    private ImpulseCsvService impulseCsvService;


    @Test
    public void testMappingToCsv() throws Exception {

        List<ContServiceDataImpulse> impulseList = new ArrayList<>();

        ContServiceDataImpulse impulse = new ContServiceDataImpulse();
        impulse.setDataDate(new Date());
        impulse.setDataValue(new BigDecimal(123));
        impulseList.add(impulse);

        String s = new String(impulseCsvService.writeDataToCsv(impulseList));
        log.info("CSV: \n{}", s);


    }

    @Test
    public void testMappingToUCsv() throws Exception {

        List<ContServiceDataImpulseUCsv> impulseList = new ArrayList<>();

        ContServiceDataImpulseUCsv impulse = new ContServiceDataImpulseUCsv();
        impulse.setDataDate(LocalDate.now());
        impulse.setDataValue(123.0);
        impulseList.add(impulse);

        String s = new String(impulseCsvService.writeDataToUCsv(impulseList));
        log.info("CSV: \n{}", s);


    }
}
