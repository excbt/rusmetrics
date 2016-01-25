package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetadataService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaDeviceObjectMetadataControllerTest extends AnyControllerTest {

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	//private final static long DEV_DEVICE_OBJECT_ID = 65836845;
	private final static long DEV_DEVICE_OBJECT_ID = 3;
	private final static long DEV_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

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
	public void testContServiceTypesGet() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects/deviceObjects/metadata/contServiceTypes"));
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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectMetadataUpdate() throws Exception {
		List<DeviceObjectMetadata> metadata = deviceObjectMetadataService
				.selectDeviceObjectMetadata(DEV_DEVICE_OBJECT_ID);

		metadata.forEach(i -> {
			i.setMetaComment("Comment by REST :" + System.currentTimeMillis());
		});

		String url = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID));
		_testJsonUpdate(url, metadata);
	}

}
