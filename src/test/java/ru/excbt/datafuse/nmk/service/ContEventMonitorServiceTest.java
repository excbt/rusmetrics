package ru.excbt.datafuse.nmk.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
public class ContEventMonitorServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ContEventMonitorServiceTest.class);

	@Autowired
	private ContEventMonitorService contEventMonitorService;


	@Autowired
	private ObjectAccessService objectAccessService;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Test
	public void testMonitor() throws Exception {

		List<ContObject> vList = objectAccessService.findContObjects(portalUserIdsService.getCurrentIds().getSubscriberId());

		for (ContObject co : vList) {
			List<ContEventMonitor> monitorList = contEventMonitorService.findByContObject(co.getId());

			if (monitorList.size() > 0) {
				logger.info("(ContObjectId:{}) Found {} monitorEvents", co.getId(), monitorList.size());
				monitorList.forEach((m) -> logger.info("Keyname:{}. eventColor: {}. eventTime:{}",
						m.getContEventType().getKeyname(), m.getContEventLevelColor().getKeyname(),
						m.getContEventTime()));
			} else {
				continue;
			}

			ContEventLevelColorKey colorKey = contEventMonitorService.getColorKeyByContObject(co.getId());
			assertNotNull(colorKey);
			logger.info("(ContObjectId:{}) findContEventMonitorColor colorKey:{}", co.getId(), colorKey.getKeyname());
		}
	}

	@Test
    @Transactional
	public void testCityContObjectStatus() throws Exception {
		Map<UUID, Long> result = contEventMonitorService
				.selectCityContObjectMonitorEventCount(portalUserIdsService.getCurrentIds().getSubscriberId());
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

}
