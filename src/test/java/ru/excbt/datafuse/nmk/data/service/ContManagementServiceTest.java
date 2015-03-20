package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.ContManagement;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContManagementService;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.support.TestConstants;

public class ContManagementServiceTest extends JpaConfigTest {

	@Autowired
	private ContManagementService contManagementService;

	@Autowired
	private ContObjectService contObjectService;

	@Test
	public void testIzhevskManagement() {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTime defaultBeginDate = formatter.parseDateTime("01/01/2014");		
		
		// contManagementService.selectActiveManagement(contObjectId,
		// TestConstants.ORGANIZATION_TEST_IZH)
		List<ContObject> list = contObjectService.findByFullName("%Ижевск%");
		assertTrue(list.size() > 0);

		for (ContObject co : list) {
			List<ContManagement> managementList = contManagementService
					.selectActiveManagement(co.getId(),
							TestConstants.ORGANIZATION_TEST_IZH);

			if (managementList.size() == 0) {
				ContManagement contManagement = contManagementService.createManagement(co.getId(),
							TestConstants.ORGANIZATION_TEST_IZH, defaultBeginDate);
				assertNotNull(contManagement);
			}
		}
		
		List<ContManagement> managementList = contManagementService.selectByOgranization(TestConstants.ORGANIZATION_TEST_IZH);
		assertTrue(managementList.size() > 0);

	}

}
