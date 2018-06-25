package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.modelmapper.DeviceObjectToDTOMapping;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class DeviceObjectServiceTest extends PortalDataTest {

	private final static Long DEV_CONT_OBJECT = 733L;

	private static final Logger log = LoggerFactory.getLogger(DeviceObjectServiceTest.class);

	@Autowired
	public DeviceObjectService deviceObjectService;

    @Autowired
	private DeviceObjectToDTOMapping deviceObjectDTOMapping;

    @Autowired
    private DeviceObjectRepository deviceObjectRepository;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


	@Test
    @Transactional
	public void testCreatePortalDeviceObject() throws Exception {
		DeviceObject deviceObject = deviceObjectService.createManualDeviceObject();
		checkNotNull(deviceObject);
		deviceObjectService.deleteManualDeviceObject(deviceObject.getId());
	}

	@Test
    @Transactional
	public void testSelectByContObject() throws Exception {
		List<?> vList = deviceObjectService.selectDeviceObjectsByContObjectId(DEV_CONT_OBJECT);
		assertTrue(vList.size() > 0);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testSubscriberDeviceObjects() throws Exception {
		List<DeviceObject> deviceObjects = deviceObjectService
				.selectDeviceObjectsBySubscriber(portalUserIdsService.getCurrentIds().getSubscriberId());
		assertTrue(deviceObjects.size() > 0);

	}

    /**
     * TODO invalid deviceObjectId
     * @throws Exception
     */
    @Test
    @Transactional
    @Ignore
    public void testFindDeviceObjectDTO() throws Exception {
	    final DeviceObject deviceObject = deviceObjectService.selectDeviceObject(128729223L);
        DeviceObjectDTO deviceObjectDTO = deviceObjectService.findDeviceObjectDTO(128729223L);
        log.info("deviceObject source {}", deviceObject);
        log.info("deviceObjectDTO source {}", deviceObjectDTO);
        deviceObjectDTO.setIsTimeSyncEnabled(!Boolean.TRUE.equals(deviceObjectDTO.getIsTimeSyncEnabled()));
        deviceObjectService.saveDeviceObjectShort(deviceObjectDTO);
        DeviceObjectDTO deviceObjectDTO2 = deviceObjectService.findDeviceObjectDTO(128729223L);

        log.info("BEFORE: {}",deviceObjectDTO);
        log.info("AFTER : {}",deviceObjectDTO2);
    }


    @Test
    public void testSubscrDataSource() {
	    DeviceObject deviceObject = deviceObjectRepository.findById(486L)
            .orElseThrow(() -> new EntityNotFoundException(DeviceObject.class, 468L));
	    log.info("ActiveDataSource, {}", deviceObject.getActiveDataSource());
	    log.info("EditDataSourceInfo, {}", deviceObject.getEditDataSourceInfo());


    }
}
