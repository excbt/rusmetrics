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
import ru.excbt.datafuse.nmk.data.service.widget.ElWidgetService;
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
 * @since 11.01.2017
 *
 */
@RunWith(SpringRunner.class)
public class ElWidgetControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private ElWidgetController elWidgetController;
    @Autowired
    private ContEventMonitorV3Service monitorService;
    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private ElWidgetService elWidgetService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        elWidgetController = new ElWidgetController(monitorService,
            contZPointService, contObjectService, elWidgetService, objectAccessService, portalUserIdsService, subscriberService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(elWidgetController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


	@Test
	public void testWidgetChartDataWeek() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/el/{id}/chart/data/week", TestWidgetConstants.EL_ZPOINT_ID).testGet();
	}

	@Test
	public void testWidgetChartDataToday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/el/{id}/chart/data/today", TestWidgetConstants.EL_ZPOINT_ID).testGet();
	}

	@Test
	public void testWidgetChartDataYesterday() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/el/{id}/chart/data/yesterday", TestWidgetConstants.EL_ZPOINT_ID).testGet();
	}

}
