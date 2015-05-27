package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ContGroupControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContGroupControllerTest.class);

	@Test
	public void testContGroupObjects() throws Exception {
		testJsonGet("/api/contGroup/0/contObject/available");
	}

	@Test
	public void testContGroup() throws Exception {
		testJsonGet("/api/contGroup");
	}

}
