package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;


@RunWith(SpringRunner.class)
public class RmaOrganizationControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaOrganizationControllerTest.class);

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

    private RmaOrganizationController rmaOrganizationController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

	    rmaOrganizationController = new RmaOrganizationController(organizationService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaOrganizationController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
    @Transactional
	public void testGetOrganizations() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/organizations").testGet();
	}

	@Test
    @Transactional
    @Ignore
	public void testCRUDOrganization() throws Exception {
		Organization organization = new Organization();
		organization.setOrganizationName("Org By AK");
		organization.setFlagRso(true);

		String url = "/api/rma/organizations";

		Long orgId = mockMvcRestWrapper.restRequest("/api/rma/organizations").testPost(organization).getLastId();
//		Long orgId = _testCreateJson(url, organization);

		logger.info("Organization ID: {}", orgId);

		organization = organizationService.findOneOrganization(orgId)
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, orgId));

		organization.setOrganizationFullAddress("FULL Address");
		organization.setOrganizationDescription("Modified By AK");

		String urlLoc = url + "/" + orgId;

        mockMvcRestWrapper.restRequest(urlLoc)
            .testPut(organization)
            .testGet()
            .testDelete();
//		_testUpdateJson(urlLoc, organization);
//
//		_testGetJson(urlLoc);
//
//		_testDeleteJson(urlLoc);

	}

	@Test
	@Ignore
    @Transactional
	public void testDeleteOrganization() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/organizations/{id1}", 489424236).testGet();
	}
}
