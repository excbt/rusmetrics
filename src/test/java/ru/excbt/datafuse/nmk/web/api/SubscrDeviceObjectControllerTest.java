package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private final static Long DEV_CONT_OBJECT = 733L;

	@Test
	public void testGetDeviceObjects() throws Exception {
		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		testJsonGet(url);
	}
}
