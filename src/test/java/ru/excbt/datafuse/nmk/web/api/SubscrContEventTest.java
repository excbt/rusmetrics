package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrContEventTest extends AnyControllerTest {

	@Test
	public void testSubscrContObjectZPoints() throws Exception {
		testJsonGet ("/api/subscr/contObjects/events");
	}

}
