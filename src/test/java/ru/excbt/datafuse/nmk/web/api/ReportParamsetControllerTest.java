package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportParamsetControllerTest extends AnyControllerTest {

	
	@Test
	public void testCommParamset() throws Exception {
		testJsonGet("/api/reportParamset/commerce");
	}
}
