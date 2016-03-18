package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class TemperatureChartControllerTest extends AnyControllerTest {

	@Test
	public void testTemperatureChartAll() throws Exception {
		_testGetJson("/api/subscr/temperatureChart");
	}
}
