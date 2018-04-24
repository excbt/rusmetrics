package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

@RunWith(SpringRunner.class)
public class DeviceObjectMetadataServiceTest extends PortalDataTest {

	private final static long RO_DEVICE_OBJECT_ID = 65836845;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	@Autowired
	private MeasureUnitService measureUnitService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
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
    @Transactional
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
    @Transactional
	public void testDelete() throws Exception {
		deviceObjectMetadataService.deleteDeviceObjectMetadata(3L);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testSameMeasureUnits() throws Exception {
		List<MeasureUnit> measureUnits = measureUnitService.selectMeasureUnitsSame("P_MPA");
		assertTrue(measureUnits.size() > 0);
	}

	@Test
    @Transactional
	public void testMetadataTransform() throws Exception {
		deviceObjectMetadataService.deviceObjectMetadataTransform(512075328L);
	}

}
