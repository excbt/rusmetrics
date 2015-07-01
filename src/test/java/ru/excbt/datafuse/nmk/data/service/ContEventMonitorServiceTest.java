package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ContEventLevelColorKey;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitor;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ContEventMonitorServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContEventMonitorServiceTest.class);

	@Autowired
	private ContEventMonitorService contEventMonitorService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testMonitor() throws Exception {

		List<ContObject> vList = subscriberService
				.selectSubscriberContObjects(currentSubscriberService
						.getSubscriberId());

		for (ContObject co : vList) {
			List<ContEventMonitor> monitorList = contEventMonitorService
					.findByContObject(co.getId());

			if (monitorList.size() > 0) {
				logger.info("(ContObjectId:{}) Found {} monitorEvents",
						co.getId(), monitorList.size());
				monitorList.forEach((m) -> logger.info(
						"Keyname:{}. eventColor: {}. eventTime:{}", m
								.getContEventType().getKeyname(), m
								.getContEventLevelColor().getKeyname(), m
								.getContEventTime()));
			} else {
				continue;
			}

			ContEventLevelColorKey colorKey = contEventMonitorService
					.getColorKeyByContObject(co.getId());
			assertNotNull(colorKey);
			logger.info(
					"(ContObjectId:{}) findContEventMonitorColor colorKey:{}",
					co.getId(), colorKey.getKeyname());
		}

	}
}
