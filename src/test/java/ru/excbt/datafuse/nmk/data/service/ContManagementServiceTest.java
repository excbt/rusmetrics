package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.support.TestConstants;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
//    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})

@RunWith(SpringRunner.class)
public class ContManagementServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ContManagementServiceTest.class);

	@Autowired
	private ContManagementService contManagementService;

	@Autowired
	private ContObjectService contObjectService;

	@Test
    @Transactional
	public void testIzhevskManagement() {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		LocalDate defaultBeginDate = formatter.parseLocalDate("01/01/2014");

		// contManagementService.selectActiveManagement(contObjectId,
		// TestConstants.ORGANIZATION_TEST_IZH)
		List<ContObject> list = contObjectService.findContObjectsByFullName("%Ижевск%");

		if (list.size() == 0) {
			return;
		}

		for (ContObject co : list) {

			logger.info("Find Izhevsk ContObject: id:{}; fullAddress:{}", co.getId(), co.getFullAddress());

			List<ContManagement> managementList = contManagementService.selectActiveManagement(co.getId());

			if (managementList.size() == 0) {
				ContManagement contManagement = contManagementService.createManagement(co.getId(),
						TestConstants.ORGANIZATION_TEST_IZH, defaultBeginDate);
				assertNotNull(contManagement);

				List<ContManagement> checkList = contManagementService.selectContManagement(co.getId(),
						TestConstants.ORGANIZATION_TEST_IZH);
				assertTrue(checkList.size() > 0);

			}
			break;
		}

	}

}
