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
import ru.excbt.datafuse.nmk.data.service.widget.HeatWidgetService;
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
 * @since 27.12.2016
 *
 */
@RunWith(SpringRunner.class)
public class HeatWidgetControllerTest extends PortalApiTest {

	/*
	 * test query

	SELECT 	cont_zpoint_id,
	   	time_detail_type,
	   	b_date,
	   	e_date,
		data_date,
		t_in,
		t_out,
		chart_t_in,
		chart_t_out,
		t_ambience
	FROM widgets.get_heat_data(107365375, '2016-03-07 23:59:59', 'WEEK');
	 */

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalContObjectMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private HeatWidgetController heatWidgetController;
	@Autowired
    private ContEventMonitorV3Service monitorService;
    @Autowired
	private ContZPointService contZPointService;
    @Autowired
    private HeatWidgetService heatWidgetService;
    @Autowired
    private ContObjectService contObjectService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscriberService subscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        heatWidgetController = new HeatWidgetController(monitorService,
            contZPointService, contObjectService, heatWidgetService, objectAccessService, portalUserIdsService, subscriberService);

	    this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(heatWidgetController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

	    mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
	}


	/**
	 * YESTERDAY, TODAY, WEEK
	 *
	 * @throws Exception
	 */
	@Test
	public void testWidgetChartData() throws Exception {
	    mockMvcRestWrapper.restRequest("/api/subscr/widgets/heat/{id}/chart/data/day", TestWidgetConstants.HEAT_ZPOINT_ID).testGet();
	}

	@Test
	public void testWidgetChartDataWeek() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/heat/{id}/chart/data/week", TestWidgetConstants.HEAT_ZPOINT_ID).testGet();
	}

	@Test
	public void testWidgetStatus() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/widgets/heat/{id}/status", TestWidgetConstants.HEAT_ZPOINT_ID).testGet();
	}

}
