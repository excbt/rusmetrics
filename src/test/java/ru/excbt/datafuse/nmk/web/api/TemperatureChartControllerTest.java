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
		_testGetJson("/api/subscr/temperatureCharts");
	}

	@Test
	public void testTemperatureChartCRUD() throws Exception {
		TemperatureChart newEntity = new TemperatureChart();
		newEntity.setChartName("TEST Name " + (new Date()));
		newEntity.setRsoOrganizationId(25201856L);
		newEntity.setLocalPlaceId(490041188L);
		Long id = _testCreateJson("/api/subscr/temperatureCharts", newEntity);

		newEntity = temperatureChartService.selectTemperatureChart(id);
		newEntity.setChartComment(EDITED_BY_REST);
		newEntity.setRsoOrganization(null);
		newEntity.setLocalPlace(null);

		_testUpdateJson("/api/subscr/temperatureCharts/" + id, newEntity);

		testTemperatureChartItems(id);

		_testDeleteJson("/api/subscr/temperatureCharts/" + id);
	}

	/**
	 * 
	 * @param temperatureChartId
	 */
	private void testTemperatureChartItems(Long temperatureChartId) throws Exception {
		String itemsUrl = "/api/subscr/temperatureCharts/" + temperatureChartId + "/items";
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
