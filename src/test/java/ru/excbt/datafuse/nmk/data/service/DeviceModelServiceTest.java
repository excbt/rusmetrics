package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class DeviceModelServiceTest extends JpaSupportTest {

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
