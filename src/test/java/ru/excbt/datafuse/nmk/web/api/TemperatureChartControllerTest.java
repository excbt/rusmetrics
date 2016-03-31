package ru.excbt.datafuse.nmk.web.api;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;
import ru.excbt.datafuse.nmk.data.service.TemperatureChartService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

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
		newItem.setT_Ambience(new BigDecimal(10));
		newItem.setT_In(new BigDecimal(11));
		newItem.setT_Out(new BigDecimal(12));
		Long id = _testCreateJson(itemsUrl, newItem);

		newItem = temperatureChartService.selectTemperatureChartItem(id);
		newItem.setT_Ambience(new BigDecimal(12));
		newItem.setItemComment("Edited By REST");
		_testUpdateJson(itemsUrl + "/" + id, newItem);
		_testDeleteJson(itemsUrl + "/" + id);
	}

}
