package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class TemperatureChartServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartServiceTest.class);

	@Autowired
	private TemperatureChartService temperatureChartService;

	@Test
	public void testSelectAll() throws Exception {
		List<TemperatureChart> result = temperatureChartService.selectTemperatureCharts();
		assertNotNull(result);
	}
}
