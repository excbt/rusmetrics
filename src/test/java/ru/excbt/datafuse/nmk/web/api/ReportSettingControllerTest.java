package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportActionTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportSheduleTypeRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;


@RunWith(SpringRunner.class)
public class ReportSettingControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @MockBean
    private PortalUserIdsService portalUserIdsService;

    private ReportSettingsController reportSettingsController;

    @Autowired
    private ReportTypeService reportTypeService;
    @Autowired
    private ReportPeriodService reportPeriodService;
    @Autowired
    private ReportSheduleTypeRepository reportScheduleService;
    @Autowired
    private ReportActionTypeRepository reportActionTypeRepository;
    @Autowired
    private SubscrServiceAccessService subscrServiceAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        reportSettingsController = new ReportSettingsController(
            reportTypeService,
            reportPeriodService,
            reportScheduleService,
            reportActionTypeRepository,
            portalUserIdsService,
            subscrServiceAccessService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(reportSettingsController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
    @Transactional
	public void testGetReportTypes() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportSettings/reportType").testGet();
//		_testGetJson("/api/reportSettings/reportType");
	}

	@Test
    @Transactional
	public void testGetReportPeriod() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportSettings/reportPeriod").testGet();
//		_testGetJson("/api/reportSettings/reportPeriod");
	}

	@Test
    @Transactional
	public void testGetReportSheduleType() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportSettings/reportSheduleType").testGet();
//		_testGetJson("/api/reportSettings/reportSheduleType");
	}

	@Test
    @Transactional
	public void testGetReportActionType() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportSettings/reportActionType").testGet();
//		_testGetJson("/api/reportSettings/reportActionType");
	}


}
