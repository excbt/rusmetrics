package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.service.TemperatureChartService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class TemperatureChartControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartControllerTest.class);

	@Autowired
	private TemperatureChartService temperatureChartService;

	@Test
	public void testTemperatureChartAll() throws Exception {
		_testGetJson("/api/subscr/temperatureChart");
	}

	@Test
	public void testTemperatureChartCRUD() throws Exception {
		TemperatureChart newEntity = new TemperatureChart();
		newEntity.setChartName("TEST Name " + (new Date()));
		newEntity.setRsoOrganizationId(25201856L);
		newEntity.setLocalPlaceId(490041188L);
		Long id = _testCreateJson("/api/subscr/temperatureChart", newEntity);

		newEntity = temperatureChartService.selectTemperatureChart(id);
		newEntity.setChartComment("Edited by REST");
		newEntity.setRsoOrganization(null);
		newEntity.setLocalPlace(null);

		_testUpdateJson("/api/subscr/temperatureChart/" + id, newEntity);

		_testDeleteJson("/api/subscr/temperatureChart/" + id);
	}

}
