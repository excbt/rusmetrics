package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.modelmapper.DeviceObjectToDTOMapping;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;


@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class DeviceObjectServiceTest extends JpaSupportTest {

	private final static Long DEV_CONT_OBJECT = 733L;

	private static final Logger log = LoggerFactory.getLogger(DeviceObjectServiceTest.class);

	@Autowired
	public DeviceObjectService deviceObjectService;

	@Autowired
	protected CurrentSubscriberService currentSubscriberService;

    @Autowired
	private DeviceObjectToDTOMapping deviceObjectDTOMapping;

    @Autowired
    private DeviceObjectRepository deviceObjectRepository;

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
				.selectDeviceObjectsBySubscriber(currentSubscriberService.getSubscriberId());
		assertTrue(deviceObjects.size() > 0);

	}

    @Test
    @Transactional
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
	    DeviceObject deviceObject = deviceObjectRepository.findOne(486L);
	    log.info("ActiveDataSource, {}", deviceObject.getActiveDataSource());
	    log.info("EditDataSourceInfo, {}", deviceObject.getEditDataSourceInfo());


    }
}
