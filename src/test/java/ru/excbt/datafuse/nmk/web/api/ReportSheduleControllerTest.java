package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportSheduleControllerTest extends AnyControllerTest {

	@Test
	public void testSheduleActive() throws Exception {
		testJsonGet("/api/reportShedule/active");
	}
	
}
