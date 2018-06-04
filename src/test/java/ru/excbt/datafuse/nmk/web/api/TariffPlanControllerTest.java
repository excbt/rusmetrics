package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class TariffPlanControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(TariffPlanControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private TariffPlanController tariffPlanController;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Autowired
    private TariffOptionRepository tariffOptionRepository;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private OrganizationService organizationService;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        tariffPlanController = new TariffPlanController(tariffPlanService,tariffOptionRepository, tariffTypeRepository, contObjectService, organizationService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(tariffPlanController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Test
	public void testOption() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/tariff/option").testGet();
	}

	@Test
	public void testType() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/tariff/type").testGet();
	}

	@Test
	public void testRso() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/tariff/rso").testGet();
	}

	@Test
	public void testDefault() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/tariff/default").testGet();
	}

	@Test
    @Ignore
	public void testUpdate() throws Exception {

		List<TariffPlan> tariffPlanList = tariffPlanService
				.selectTariffPlanList(currentSubscriberService.getSubscriberId());
		if (tariffPlanList.size() == 0) {
			logger.warn("Skip Tarif Plan List");
			// return;
		}
		assertTrue(tariffPlanList.size() > 0);

		TariffPlan testRec = tariffPlanList.get(0);
		if (testRec.getTariffPlanValue() != null) {
			testRec.setTariffPlanValue(testRec.getTariffPlanValue().add(BigDecimal.valueOf(0.1)));
		} else {
			testRec.setTariffPlanValue(BigDecimal.valueOf(0.1));
		}
		String urlStr = "/api/subscr/tariff/" + testRec.getId();
		String jsonBody = TestUtils.objectToJson(testRec);

		List<ContObject> tariffContObjects = tariffPlanService.selectTariffPlanContObjects(testRec.getId(),
				currentSubscriberService.getSubscriberId());

		for (ContObject co : tariffContObjects) {
			logger.info("ContObject (id={}). FullAddress: {}", co.getId(), co.getFullAddress());
		}

		long[] contObjects = new long[tariffContObjects.size()];
		int idx = 0;
		for (ContObject co : tariffContObjects) {
			contObjects[idx++] = co.getId();
		}

		ResultActions resultActionsAll;
		try {
			resultActionsAll = restPortalMockMvc.perform(put(urlStr).contentType(MediaType.APPLICATION_JSON)
					.param("rsoOrganizationId", testRec.getRso().getId().toString())
					.param("tariffTypeId", testRec.getTariffType().getId().toString())
					.param("contObjectIds", TestUtils.arrayToString(contObjects)).content(jsonBody).with(testSecurityContext())
					.accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
    @Ignore
	public void testCreate() throws Exception {
		TariffPlan tariffPlan = new TariffPlan();
		tariffPlan.setTariffPlanValue(BigDecimal.valueOf(1.1));
		tariffPlan.setTariffPlanComment("Created by REST");
		tariffPlan.setStartDate(LocalDate.now().toDate());

		String urlStr = "/api/subscr/tariff";
		String jsonBody = TestUtils.objectToJson(tariffPlan);

		Iterable<Organization> orgList = subscriberRepository
				.selectRsoOrganizations(currentSubscriberService.getSubscriberId());

		assertTrue(orgList.iterator().hasNext());
		Organization org = orgList.iterator().next();

		List<TariffType> tariffTypeList = tariffTypeRepository.findByContServiceType("cw");

		assertTrue(tariffTypeList.size() > 0);
		TariffType tt = tariffTypeList.get(0);

		List<ContObject> tariffContObjects = tariffPlanService.selectTariffPlanAvailableContObjects(0,
				currentSubscriberService.getSubscriberId());

		assertTrue(tariffContObjects.size() > 0);
		long[] contObjectIds = new long[1];
		contObjectIds[0] = tariffContObjects.get(0).getId();

		ResultActions resultAction = restPortalMockMvc.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
				.param("rsoOrganizationId", org.getId().toString()).param("tariffTypeId", tt.getId().toString())
				.param("contObjectIds", TestUtils.arrayToString(contObjectIds)).content(jsonBody).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

	}

	@Test
	public void testAvailableContObjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/tariff/1/contObject/available").testGet();
	}

}
