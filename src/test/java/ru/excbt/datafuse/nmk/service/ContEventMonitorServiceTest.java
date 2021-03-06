package ru.excbt.datafuse.nmk.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;


//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
//    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
//@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class ContEventMonitorServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ContEventMonitorServiceTest.class);

	@Autowired
	private ContEventMonitorService contEventMonitorService;

//	@Autowired
//	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private ObjectAccessService objectAccessService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testMonitor() throws Exception {

		List<ContObject> vList = objectAccessService.findContObjects(currentSubscriberService.getSubscriberId());

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
				.selectCityContObjectMonitorEventCount(currentSubscriberService.getSubscriberId());
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

}
