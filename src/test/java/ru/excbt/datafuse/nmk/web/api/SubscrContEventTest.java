package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrContEventTest extends AnyControllerTest {

	@Test
	@Ignore
	public void testSubscrContObjectEvents() throws Exception {
		testJsonGet ("/api/subscr/contObjects/events");
	}

}
