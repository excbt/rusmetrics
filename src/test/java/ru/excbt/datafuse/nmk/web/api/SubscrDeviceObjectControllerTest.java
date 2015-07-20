package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	@Test
	public void testGetDeviceObjects() throws Exception {
		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		testJsonGet(url);
	}

	@Test
	public void testGetDeviceObjectMetaDataVzlet() throws Exception {
		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT,
				DEV_DEVICE_OBJECT));
		testJsonGet(url);
	}
}
