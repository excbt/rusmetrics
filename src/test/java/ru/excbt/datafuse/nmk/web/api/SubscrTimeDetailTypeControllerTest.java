package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrTimeDetailTypeControllerTest extends AnyControllerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTimeDetailType1h24h() throws Exception {
		_testGetJson(apiSubscrUrl("/timeDetailType/1h24h"));
	}
}
