package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;


@Transactional
public class RmaOrganizationControllerTest extends RmaControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaOrganizationControllerTest.class);

	@Autowired
	private OrganizationService organizationService;

	@Test
    @Transactional
	public void testGetOrganizations() throws Exception {
		String url = "/api/rma/organizations";
		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testCRUDOrganization() throws Exception {
		Organization organization = new Organization();
		organization.setOrganizationName("Org By AK");
		organization.setFlagRso(true);

		String url = "/api/rma/organizations";
		Long orgId = _testCreateJson(url, organization);

		logger.info("Organization ID: {}", orgId);

		organization = organizationService.findOneOrganization(orgId)
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, orgId));

		organization.setOrganizationFullAddress("FULL Address");
		organization.setOrganizationDescription("Modified By AK");

		String urlLoc = url + "/" + orgId;

		_testUpdateJson(urlLoc, organization);

		_testGetJson(urlLoc);

		_testDeleteJson(urlLoc);

	}

	@Test
	@Ignore
    @Transactional
	public void testDeleteOrganization() throws Exception {
		String url = "/api/rma/organizations/489424236";
		_testDeleteJson(url);
	}
}
