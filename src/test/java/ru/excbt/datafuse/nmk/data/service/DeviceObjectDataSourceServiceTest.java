package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class DeviceObjectDataSourceServiceTest extends PortalDataTest {

	private final static long TEST_DEVICE_OBJECT_ID = 65836845;
	private final static long SUBSCR_USER_ID = 64166469; // manual-ex1
	public static final long SUBSCR_ORG_ID = 64166467; // РМА-EXCBT

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceObjectDataSourceService deviceObjectDataSourceService;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;


	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
    @Ignore
	public void testDeviceObjectDataSource() throws Exception {
		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(TEST_DEVICE_OBJECT_ID);
		assertNotNull(deviceObject);
		SubscrDataSource subscrDataSource = subscrDataSourceService
				.selectDataSourceByKeyname(portalUserIdsService.getCurrentIds().getSubscriberId(), "DEVICE_DEFAULT");// DEVICE_DEFAULT
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
	public long getSubscriberId() {
		return SUBSCR_ORG_ID;
	}

	/**
	 *
	 * @return
	 */
	public long getSubscrUserId() {
		return SUBSCR_USER_ID;
	}

}
