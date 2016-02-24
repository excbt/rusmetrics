package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrDeviceObjectPkeControllerTest extends AnyControllerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectPkeTypes() throws Exception {
		String url = apiSubscrUrl("/deviceObjects/pke/types");
		_testGetJson(url);
	}
}
