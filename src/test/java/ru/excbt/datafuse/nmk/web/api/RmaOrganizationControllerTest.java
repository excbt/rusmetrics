package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaOrganizationControllerTest extends RmaControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaOrganizationControllerTest.class);

	@Autowired
	private OrganizationService organizationService;

	@Test
	public void testGetOrganizations() throws Exception {
		String url = "/api/rma/organizations";
		_testGetJson(url);
	}

	@Test
	public void testCRUDOrganization() throws Exception {
		Organization organization = new Organization();
		organization.setOrganizationName("Org By AK");
		organization.setFlagRso(true);

		String url = "/api/rma/organizations";
		Long orgId = _testCreateJson(url, organization);

		logger.info("Organization ID: {}", orgId);

		organization = organizationService.findOrganization(orgId);
		organization.setOrganizationFullAddress("FULL Address");
		organization.setOrganizationDecription("Modified By AK");

		String urlLoc = url + "/" + orgId;

		_testUpdateJson(urlLoc, organization);

		_testGetJson(urlLoc);

		_testDeleteJson(urlLoc);

	}

	@Test
	@Ignore
	public void testDeleteOrganization() throws Exception {
		String url = "/api/rma/organizations/489424236";
		_testDeleteJson(url);
	}
}
