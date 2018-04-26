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
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.widget.HwWidgetService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ContEventMonitorV3Service;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 *
 */
@RunWith(SpringRunner.class)
public class HwWidgetControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private HwWidgetController hwWidgetController;

    @Autowired
    private ContEventMonitorV3Service monitorService;
    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private HwWidgetService hwWidgetService;
    @Autowired
    private ContServiceDataHWaterService contServiceDataHWaterService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscriberService subscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        hwWidgetController = new HwWidgetController(monitorService,
            contZPointService, contObjectService, hwWidgetService, contServiceDataHWaterService, objectAccessService, portalUserIdsService, subscriberService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(hwWidgetController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


	@Test
	public void testHwWidgetStatus() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/hw/{id}/status", TestWidgetConstants.HW_ZPOINT_ID).testGet();
	}

	@Test
	public void testHwWidgetChartDataWeek() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/hw/{id}/chart/data/week", TestWidgetConstants.HW_ZPOINT_ID).testGet();
	}

	@Test
	public void testHwWidgetChartDataYesterday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/hw/{id}/chart/data/yesterday", TestWidgetConstants.HW_ZPOINT_ID).testGet();
	}

	@Test
	public void testHwWidgetChartDataToday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/hw/{id}/chart/data/today", TestWidgetConstants.HW_ZPOINT_ID).testGet();
	}

}
