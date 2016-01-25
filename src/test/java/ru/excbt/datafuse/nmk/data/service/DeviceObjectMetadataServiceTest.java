package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;

public class DeviceObjectMetadataServiceTest extends JpaSupportTest {

	private final static long RO_DEVICE_OBJECT_ID = 65836845;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		List<DeviceObjectMetadata> metadataList = deviceObjectMetadataService
				.selectDeviceObjectMetadata(RO_DEVICE_OBJECT_ID);

		metadataList.forEach(i -> {
			i.setMetaComment("Comment millis = " + System.currentTimeMillis());
		});

		deviceObjectMetadataService.updateDeviceObjectMetadata(RO_DEVICE_OBJECT_ID, metadataList);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeepCopy() throws Exception {
		List<DeviceObjectMetadata> result = deviceObjectMetadataService.copyDeviceObjectMetadata(RO_DEVICE_OBJECT_ID,
				3L);
		result.forEach(i -> {
			assertFalse(i.isNew());
		});
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDelete() throws Exception {
		deviceObjectMetadataService.deleteDeviceObjectMetadata(3L);
	}

}
