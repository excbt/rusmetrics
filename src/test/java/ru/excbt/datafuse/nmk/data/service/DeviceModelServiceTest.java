package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;

public class DeviceModelServiceTest extends JpaSupportTest {

	@Autowired
	private DeviceModelService deviceModelService;
	
	@Test
	@Ignore
	public void testDeviceModelService() {
		assertNotNull(deviceModelService);
		
		DeviceModel entity = new DeviceModel();
		
		entity.setExCode("CODE_EX");
		entity.setModelName("Model1");
		//entity.setRowAudit(RowAudit.newInstanceNow());
		
		entity = deviceModelService.save(entity);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		deviceModelService.delete(entity.getId());
		
	}

}
