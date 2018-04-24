package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

/**
 * Created by kovtonyk on 08.06.2017.
 */
@RunWith(SpringRunner.class)
public class CabinetOutServiceTest extends PortalDataTest {

    @Autowired
    private CabinetOutService cabinetOutService;

    @Test
    public void testCabinetOut() throws Exception {
        cabinetOutService.importCabinetOut();
    }
}
