package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrTest extends AnyControllerTest {

	@Test
	public void testSubscrContObjects() throws Exception {
		_testJsonGet ("/api/subscr/contObjects");
	}

	@Test
	public void testSubscrContObjectZPoints() throws Exception {
		_testJsonGet ("/api/subscr/contObjects/725/zpoints");
	}

	@Test
	public void testSubscrContEvents() throws Exception {
		_testJsonGet ("/api/subscr/contObjects/725/events");
	}

}
