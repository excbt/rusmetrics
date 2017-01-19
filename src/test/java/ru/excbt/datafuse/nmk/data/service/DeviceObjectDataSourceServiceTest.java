package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class DeviceObjectDataSourceServiceTest extends JpaConfigTest {

	private final static long TEST_DEVICE_OBJECT_ID = 65836845;
	private final static long SUBSCR_USER_ID = 64166469; // manual-ex1
	public static final long SUBSCR_ORG_ID = 64166467; // РМА-EXCBT

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
	@Ignore
	public void testDeviceObjectDataSource() throws Exception {
		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(TEST_DEVICE_OBJECT_ID);
		assertNotNull(deviceObject);
		SubscrDataSource subscrDataSource = subscrDataSourceService
				.selectDataSourceByKeyname(currentSubscriberService.getSubscriberId(), "DEVICE_DEFAULT");// DEVICE_DEFAULT
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

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return SUBSCR_ORG_ID;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return SUBSCR_USER_ID;
	}

}
