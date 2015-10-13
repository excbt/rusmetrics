package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaSubscriberControllerTest extends RmaControllerTest {

	@Test
	public void testGetSubscribers() throws Exception {
		_testJsonGet(apiRmaUrl("/subscribers"));
	}

	@Test
	public void testGetSubscriber() throws Exception {
		_testJsonGet(apiRmaUrl("/subscribers/64166467"));
	}
}
