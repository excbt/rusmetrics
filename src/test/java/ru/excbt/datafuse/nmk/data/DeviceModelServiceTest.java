package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.domain.RowAudit;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.service.DeviceModelService;

public class DeviceModelServiceTest extends JpaConfigTest {

	@Autowired
	private DeviceModelService deviceModelService;
	
	@Test
	@Ignore
	public void testDeviceModelService() {
		assertNotNull(deviceModelService);
		
		DeviceModel entity = new DeviceModel();
		
		entity.setExCode("CODE_EX");
		entity.setModelName("Model1");
		entity.setRowAudit(RowAudit.newInstanceNow());
		
		entity = deviceModelService.save(entity);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		deviceModelService.delete(entity.getId());
		
	}

}
