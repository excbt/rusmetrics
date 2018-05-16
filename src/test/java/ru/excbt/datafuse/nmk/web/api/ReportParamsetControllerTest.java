package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;


@RunWith(SpringRunner.class)
public class ReportParamsetControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetControllerTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;
	private static final long TEMPLATE_PARAMSET_ID = 28344056;

	private final static long TEST_PARAMSET_COMMERCE = 28618264;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalContObjectMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

	private ReportParamsetController reportParamsetController;

	@Autowired
    private ObjectAccessService objectAccessService;

    @Autowired
    private SubscrServiceAccessService subscrAccessService;

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Autowired
    private ReportTypeService reportTypeService;

    @Autowired
    private ReportParamsetService reportParamsetService;

    @Autowired
    private ReportParamsetRepository reportParamsetRepository;

    @Autowired
    private ReportParamsetParamSpecialRepository reportParamsetParamSpecialRepository;

    @Autowired
    private SubscrServiceAccessRepository subscrServiceAccessRepository;
    @Autowired
    private SubscrServiceItemRepository subscrServiceItemRepository;
    @Autowired
    private SubscrServicePackRepository subscrServicePackRepository;
    @Autowired
    private SubscrServicePermissionRepository subscrServicePermissionRepository;
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private SubscriberService subscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Autowired
    private SubscriberTimeService subscriberTimeService;


    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

//	    subscrAccessService = new SubscrServiceAccessService(
//	        subscrServiceAccessRepository,
//            subscrServiceItemRepository,
//            subscrServicePackRepository,
//            subscrServicePermissionRepository,
//            subscriberRepository,
//            subscriberTimeService);

        reportParamsetController = new ReportParamsetController(reportParamsetService, reportTemplateService, reportTypeService, objectAccessService, portalUserIdsService, subscrAccessService);

	    this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(reportParamsetController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

	    mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
	}



    /*

     */
	@Test
    @Transactional
	public void testCommerceList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportParamset/commerce").testGet();
	}

	/*

	 */
	@Test
    @Transactional
	public void testCommerceGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportParamset/commerce/{id}", TEST_PARAMSET_COMMERCE).testGet();
	}

	/*

	 */
	// TODO make test for Update & Delete
	@Test
    @Transactional
	public void testCommerceCreateUpdateDelete() throws Exception {
		List<ReportTemplate> commTemplates = reportTemplateService
				.selectDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT, true);

		assertTrue(commTemplates.size() > 0);

		ReportTemplate rt = commTemplates.get(0);

		ReportParamset reportParamset = new ReportParamset();
		reportParamset.set_active(true);
		reportParamset.setActiveStartDate(new Date());
		reportParamset.setName("Created by REST");
		reportParamset.setOutputFileType(ReportOutputFileType.PDF);
		reportParamset.setReportPeriodKey(ReportPeriodKey.LAST_MONTH);

		List<ReportMetaParamSpecial> metaParamSpecial = reportTypeService
				.findReportMetaParamSpecialList(ReportTypeKey.COMMERCE_REPORT);

		assertTrue(metaParamSpecial.size() > 0);

		{
			ReportParamsetParamSpecial param = ReportParamsetParamSpecial.newInstance(metaParamSpecial.get(0));
			param.setReportParamset(reportParamset);
			param.setTextValue("testValue");
			assertTrue(param.isOneValueAssigned());

			reportParamset.getParamSpecialList().add(param);
		}

		RequestExtraInitializer extraInializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("reportTemplateId", rt.getId().toString());

			}
		};

		String urlStr = "/api/reportParamset/commerce";

		String objString = TestUtils.objectToJson(reportParamset);
		logger.info("objString: {}", objString);

		Long createdId = mockMvcRestWrapper.restRequest(urlStr)
            .requestBuilder(b -> b.param("reportTemplateId", rt.getId().toString())).testPost(reportParamset).getLastId();

		ReportParamset reportParamsetNew = reportParamsetService.findReportParamset(createdId);

		assertTrue(reportParamsetNew.getParamSpecialList().size() == 1);
	}


	/*

	 */
	@Test
    @Transactional
	public void testUpdateUnitParamset() throws Exception {

		long[] objectIds = { 18811504L, 18811505L };

		logger.info("Array of {}", TestUtils.arrayToString(objectIds));

		ResultActions resultAction = restPortalContObjectMockMvc
				.perform(put("/api/reportParamset/{id}/contObjects", TEMPLATE_PARAMSET_ID)
						.contentType(MediaType.APPLICATION_JSON)
                        .param("contObjectIds", TestUtils.arrayToString(objectIds))
                        .accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().is2xxSuccessful());

	}

	/*

	 */
	@Test
    @Transactional
	public void testReportParamsetContextLaunch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportParamset/menu/contextLaunch").testGet();
	}

	/*

	 */
	@Test
    @Transactional
	public void testReportDirectoryItems() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportParamset/directoryParamItems/param_directory_mass_volume_switch").testGet();
	}

}
