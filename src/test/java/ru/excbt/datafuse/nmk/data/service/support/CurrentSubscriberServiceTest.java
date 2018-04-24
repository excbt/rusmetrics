package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDateTime;
import org.junit.Ignore;
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
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
@Ignore
public class CurrentSubscriberServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(CurrentSubscriberServiceTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testSubscriberId() {
		assertTrue(currentSubscriberService.getSubscriberId() > 0);
	}

	@Ignore
	@Test
	public void testFullUserInfo() {
		V_FullUserInfo result = currentSubscriberService.getFullUserInfo();
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
		LocalDateTime subscriberTime = currentSubscriberService.getSubscriberCurrentTime_Joda();
		assertNotNull(subscriberTime);
		logger.info("Subscriber Current Time: {}", subscriberTime);
	}


}
