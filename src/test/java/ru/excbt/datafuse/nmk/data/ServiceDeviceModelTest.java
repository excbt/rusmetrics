package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.RowAudit;
import ru.excbt.datafuse.nmk.data.service.DeviceModelService;
import ru.excbt.datafuse.nmk.web.JpaConfigTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/spring/jpa-config.xml")
public class ServiceDeviceModelTest extends JpaConfigTest {

	@Autowired
	private DeviceModelService deviceModelService;
	
	@Test
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
