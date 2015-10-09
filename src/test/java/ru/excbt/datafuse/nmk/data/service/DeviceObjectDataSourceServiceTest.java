package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.ManualJpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class DeviceObjectDataSourceServiceTest extends ManualJpaConfigTest {

	private final static long TEST_DEVICE_OBJECT_ID = 63028149;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceObjectDataSourceService deviceObjectDataSourceService;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectDataSource() throws Exception {
		DeviceObject deviceObject = deviceObjectService.findOne(TEST_DEVICE_OBJECT_ID);
		assertNotNull(deviceObject);
		SubscrDataSource subscrDataSource = subscrDataSourceService
				.selectByKeyname(currentSubscriberService.getSubscriberId(), "DEVICE_DEFAULT");// DEVICE_DEFAULT
																								// DEVICE_OPTION
		assertNotNull(subscrDataSource);

		DeviceObjectDataSource deviceObjectDataSource = new DeviceObjectDataSource();
		deviceObjectDataSource.setDeviceObject(deviceObject);
		deviceObjectDataSource.setSubscrDataSource(subscrDataSource);
		deviceObjectDataSource.setIsActive(true);
		deviceObjectDataSource.setSubscrDataSourceAddr("Addr+" + System.currentTimeMillis());

		deviceObjectDataSource = deviceObjectDataSourceService.saveDeviceDataSource(deviceObjectDataSource);
		checkNotNull(deviceObjectDataSource);
		checkNotNull(deviceObjectDataSource.getId());
	}
}
