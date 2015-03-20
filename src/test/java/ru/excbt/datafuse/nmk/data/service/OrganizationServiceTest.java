package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.support.TestConstants;

public class OrganizationServiceTest extends JpaConfigTest {

	
	@Autowired
	private OrganizationService organizationService;
	
	@Test
	public void testOrg() {
		assertNotNull(organizationService);
		Organization org = organizationService.findOne(TestConstants.ORGANIZATION_TEST);
		assertNotNull(org);
	}

	@Test
	public void testOrgIzh() {
		assertNotNull(organizationService);
		Organization org = organizationService.findOne(TestConstants.ORGANIZATION_TEST_IZH);
		assertNotNull(org);
	}

}
