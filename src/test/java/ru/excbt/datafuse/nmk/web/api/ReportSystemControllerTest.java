package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportSystemControllerTest extends AnyControllerTest {

	@Test
	public void testCommerceReportColumnSettings() throws Exception {
		testJsonGet("/api/reportSystem/columnSettings/commerce");
	}
}
