package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

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
	public void testName() throws Exception {
		List<DeviceObjectMetadata> metadataList = deviceObjectMetadataService
				.selectDeviceObjectMetadata(RO_DEVICE_OBJECT_ID);

		deviceObjectMetadataService.updateDeviceObjectMetadata(RO_DEVICE_OBJECT_ID, metadataList);
	}
}
