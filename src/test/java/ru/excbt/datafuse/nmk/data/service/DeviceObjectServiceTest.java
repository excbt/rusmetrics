package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

public class DeviceObjectServiceTest extends JpaSupportTest {

	@Autowired
	public DeviceObjectService deviceObjectService;

	@Test
	public void testCreatePortalDeviceObject() throws Exception {
		DeviceObject deviceObject = deviceObjectService
				.createPortalDeviceObject();
		checkNotNull(deviceObject);
		deviceObjectService.deleteOne(deviceObject.getId());
	}
}
