package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaDeviceObjectMetadataControllerTest extends AnyControllerTest {

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	private final static long DEV_DEVICE_OBJECT_ID = 63028149;
	private final static long DEV_CONT_OBJECT_ID = 63030238;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMeasureUnitGet() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects/deviceObjects/metadata/measureUnits"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectMetadataGet() throws Exception {
		String url = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID));
		_testJsonGet(url);
	}
}
