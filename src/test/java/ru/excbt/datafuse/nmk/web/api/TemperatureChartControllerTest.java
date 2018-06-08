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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.TemperatureChartService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Date;

@RunWith(SpringRunner.class)
public class TemperatureChartControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

    @Autowired
    private TemperatureChartService temperatureChartService;

    private TemperatureChartController temperatureChartController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        temperatureChartController = new TemperatureChartController(temperatureChartService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(temperatureChartController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}



	@Test
	public void testTemperatureChartAll() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/temperatureCharts").testGet();
	}

	@Test
	public void testTemperatureChartContObjectId() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/temperatureCharts/byContObject/{id}", 18811504).testGet();
	}

	@Test
    @Ignore
	public void testTemperatureChartCRUD() throws Exception {
		TemperatureChart newEntity = new TemperatureChart();
		newEntity.setChartName("TEST Name " + (new Date()));
		newEntity.setRsoOrganizationId(25201856L);
		newEntity.setLocalPlaceId(490041188L);
		newEntity.setIsOk(true);

        Long id = mockMvcRestWrapper.restRequest("/api/rma/temperatureCharts").testPost(newEntity).getLastId();

		newEntity = temperatureChartService.selectTemperatureChart(id);
		newEntity.setChartComment("New Edit");
		newEntity.setRsoOrganization(null);
		newEntity.setLocalPlace(null);

        mockMvcRestWrapper.restRequest("/api/rma/temperatureCharts/{id}", id).testPut(newEntity);

		testTemperatureChartItems(id);

        mockMvcRestWrapper.restRequest( "/api/rma/temperatureCharts/{id}", id).testDelete();
	}

	/**
	 *
	 * @param temperatureChartId
	 */
	private void testTemperatureChartItems(Long temperatureChartId) throws Exception {
		String itemsUrl = "/api/rma/temperatureCharts/" + temperatureChartId + "/items";

        mockMvcRestWrapper.restRequest(itemsUrl).testGet();
		TemperatureChartItem newItem = new TemperatureChartItem();
		newItem.setTemperatureChartId(temperatureChartId);
		newItem.setT_Ambience(10D);
		newItem.setT_In(11D);
		newItem.setT_Out(12D);

        Long id = mockMvcRestWrapper.restRequest( itemsUrl).testPost(newItem).getLastId();

		newItem = temperatureChartService.selectTemperatureChartItem(id);
		newItem.setT_Ambience(12D);
		newItem.setItemComment("Edited By REST");
        mockMvcRestWrapper.restRequest(itemsUrl+"/{id}", id).testPut(newItem);
        mockMvcRestWrapper.restRequest(itemsUrl+"/{id}", id).testDelete();
	}

}
