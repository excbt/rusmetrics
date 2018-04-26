package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class DeviceModelServiceTest extends PortalDataTest {

	@Autowired
	private DeviceModelService deviceModelService;

	@Test
    @Transactional
	public void testDeviceModelService() {
		assertNotNull(deviceModelService);

		DeviceModel entity = new DeviceModel();

		entity.setExCode("CODE_EX");
		entity.setModelName("Model1");
		// entity.setRowAudit(RowAudit.newInstanceNow());

		entity = deviceModelService.save(entity);
		assertNotNull(entity);
		assertNotNull(entity.getId());
		deviceModelService.delete(entity.getId());
	}

	@Test
    @Transactional
	public void testDeviceModelPortal() throws Exception {
		DeviceModel deviceModel = deviceModelService.findPortalDeviceModel();
		checkNotNull(deviceModel);
	}

}
