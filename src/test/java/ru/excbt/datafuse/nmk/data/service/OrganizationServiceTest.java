package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.support.TestConstants;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class OrganizationServiceTest extends JpaSupportTest {


	@Autowired
	private OrganizationService organizationService;

	@Test
    @Transactional
	public void testOrg() {
		assertNotNull(organizationService);
		Organization org = organizationService.selectOrganization(TestConstants.ORGANIZATION_TEST);
		assertNotNull(org);
	}

	@Test
    @Transactional
	public void testOrgIzh() {
		assertNotNull(organizationService);
		Organization org = organizationService.selectOrganization(TestConstants.ORGANIZATION_TEST_IZH);
		assertNotNull(org);
	}

}
