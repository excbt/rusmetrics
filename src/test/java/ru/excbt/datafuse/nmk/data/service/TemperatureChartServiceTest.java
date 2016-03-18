package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;

public class TemperatureChartServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureChartServiceTest.class);

	@Autowired
	private TemperatureChartService temperatureChartService;

	@Test
	public void testSelectAll() throws Exception {
		List<TemperatureChart> result = temperatureChartService.selectTemperatureChartAll();
		assertNotNull(result);
	}
}
