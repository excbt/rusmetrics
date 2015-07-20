package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

public class DeviceObjectServiceTest extends JpaSupportTest {

	private final static Long DEV_CONT_OBJECT = 733L;

	@Autowired
	public DeviceObjectService deviceObjectService;

	@Test
	public void testCreatePortalDeviceObject() throws Exception {
		DeviceObject deviceObject = deviceObjectService
				.createPortalDeviceObject();
		checkNotNull(deviceObject);
		deviceObjectService.deleteOne(deviceObject.getId());
	}

	@Test
	public void testSelectByContObject() throws Exception {
		List<?> vList = deviceObjectService
				.selectDeviceObjectsByContObjectId(DEV_CONT_OBJECT);
		assertTrue(vList.size() > 0);
	}
}
