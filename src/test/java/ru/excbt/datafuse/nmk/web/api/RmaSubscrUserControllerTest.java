package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaSubscrUserControllerTest extends RmaControllerTest {

	@Test
	public void testSubscrUsersGet() throws Exception {
		_testJsonGet(apiRmaUrl("/" + 64166467 + "/subscrUsers"));
	}
}
