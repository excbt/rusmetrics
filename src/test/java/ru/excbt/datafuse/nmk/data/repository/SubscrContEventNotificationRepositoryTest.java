package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class SubscrContEventNotificationRepositoryTest extends JpaSupportTest {

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectContObjectsNotificationsCountList() throws Exception {

		List<Long> vList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository
				.selectNotificatoinsCountList(
						currentSubscriberService.getSubscriberId(), vList,
						dp.getDateFrom(), dp.getDateTo());

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectContObjectsNotificationsNewCountList()
			throws Exception {

		List<Long> vList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository
				.selectNotificatoinsCountList(
						currentSubscriberService.getSubscriberId(), vList,
						dp.getDateFrom(), dp.getDateTo(), Boolean.TRUE);

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}
}
