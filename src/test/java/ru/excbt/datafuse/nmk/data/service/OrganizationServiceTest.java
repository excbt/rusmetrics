package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.support.TestConstants;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class OrganizationServiceTest extends PortalDataTest {


	@Autowired
	private OrganizationService organizationService;

	@Test
    @Transactional
	public void testOrg() {
		assertNotNull(organizationService);
		Optional<Organization> org = organizationService.findOneOrganization(TestConstants.ORGANIZATION_TEST);
		assertTrue(org.isPresent());
	}

	@Test
    @Transactional
	public void testOrgIzh() {
		assertNotNull(organizationService);
        Optional<Organization> org = organizationService.findOneOrganization(TestConstants.ORGANIZATION_TEST);
        assertTrue(org.isPresent());
	}

}
