package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.FullUserInfo;

public class CurrentSubscriberServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentSubscriberServiceTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private CurrentUserService currentUserService;

	@Test
	public void testSubscriberId() {
		assertTrue(currentSubscriberService.getSubscriberId() > 0);
	}

	@Test
	public void testFullUserInfo() {
		FullUserInfo result = currentUserService.getFullUserInfo();
		assertNotNull(result);

		logger.info("userId = {}", result.getId());

		// assertEquals("developer_user", result.getUserName());

		assertEquals(true, result.is_system());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscriberCurrentTime() throws Exception {
		LocalDateTime subscriberTime = currentSubscriberService
				.getSubscriberCurrentTime_Joda();
		assertNotNull(subscriberTime);
		logger.info("Subscriber Current Time: {}", subscriberTime);
	}

}
