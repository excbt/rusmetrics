package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscriberControllerTest extends AnyControllerTest {

	@Test
	public void testSubscriberContObjectCount() throws Exception {
		_testGetJson(apiSubscrUrl("/info/subscriberContObjectCount"));
	}
}
