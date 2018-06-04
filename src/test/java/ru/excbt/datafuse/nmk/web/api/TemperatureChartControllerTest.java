package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;
import ru.excbt.datafuse.nmk.data.service.TemperatureChartService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import java.util.Date;

@Transactional
public class TemperatureChartControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartControllerTest.class);

	@Autowired
	private TemperatureChartService temperatureChartService;

	@Test
	public void testTemperatureChartAll() throws Exception {
		_testGetJson(API_RMA + "/temperatureCharts");
	}

	@Test
	public void testTemperatureChartContObjectId() throws Exception {
		_testGetJson(API_RMA + "/temperatureCharts/byContObject/" + 18811504);
	}

	@Test
    @Ignore
	public void testTemperatureChartCRUD() throws Exception {
		TemperatureChart newEntity = new TemperatureChart();
		newEntity.setChartName("TEST Name " + (new Date()));
		newEntity.setRsoOrganizationId(25201856L);
		newEntity.setLocalPlaceId(490041188L);
		newEntity.setIsOk(true);
		Long id = _testCreateJson(API_RMA + "/temperatureCharts", newEntity);

		newEntity = temperatureChartService.selectTemperatureChart(id);
		newEntity.setChartComment(EDITED_BY_REST);
		newEntity.setRsoOrganization(null);
		newEntity.setLocalPlace(null);

		_testUpdateJson(API_RMA + "/temperatureCharts/" + id, newEntity);

		testTemperatureChartItems(id);

		_testDeleteJson(API_RMA + "/temperatureCharts/" + id);
	}

	/**
	 *
	 * @param temperatureChartId
	 */
	private void testTemperatureChartItems(Long temperatureChartId) throws Exception {
		String itemsUrl = API_RMA + "/temperatureCharts/" + temperatureChartId + "/items";
		_testGetJson(itemsUrl);
		TemperatureChartItem newItem = new TemperatureChartItem();
		newItem.setTemperatureChartId(temperatureChartId);
		newItem.setT_Ambience(10D);
		newItem.setT_In(11D);
		newItem.setT_Out(12D);
		Long id = _testCreateJson(itemsUrl, newItem);

		newItem = temperatureChartService.selectTemperatureChartItem(id);
		newItem.setT_Ambience(12D);
		newItem.setItemComment("Edited By REST");
		_testUpdateJson(itemsUrl + "/" + id, newItem);
		_testDeleteJson(itemsUrl + "/" + id);
	}

}
