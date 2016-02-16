package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ContEventControllerTest extends AnyControllerTest {

	@Test
	public void testEventTypes() throws Exception {
		_testGetJson("/api/contEvent/types");
	}
}
