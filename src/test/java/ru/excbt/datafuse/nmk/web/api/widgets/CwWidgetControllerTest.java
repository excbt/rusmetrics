/**
 *
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.widget.CwWidgetService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.01.2017
 *
 */
@RunWith(SpringRunner.class)
public class CwWidgetControllerTest extends PortalApiTest {


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private CwWidgetController cwWidgetController;

    @Autowired
    private ContEventMonitorV3Service monitorService;
    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private CwWidgetService cwWidgetService;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private SubscriberTimeService subscriberTimeService;
    @Autowired
    private ObjectAccessService objectAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        cwWidgetController = new CwWidgetController(monitorService,
            contZPointService,
            cwWidgetService,
            contObjectService,
            objectAccessService,
            portalUserIdsService,
            subscriberTimeService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(cwWidgetController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


	@Test
	public void testWidgetChartDataWeek() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/cw/{id}/chart/data/week",
            TestWidgetConstants.CW_ZPOINT_ID).testGet();
	}

	@Test
	public void testWidgetChartDataToday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/cw/{id}/chart/data/today", TestWidgetConstants.CW_ZPOINT_ID)
            .testGet();
	}

	@Test
	public void testWidgetChartDataYesterday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/cw/{id}/chart/data/yesterday", TestWidgetConstants.CW_ZPOINT_ID).testGet();
	}

}
