package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class SubscrContEventNotificationRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationRepositoryTest.class);

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	private Long subscriberId() {
		return currentSubscriberService.getSubscriberId();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectContObjectsNotificationsCountList() throws Exception {

		List<Long> vList = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository.selectNotificatoinsCountList(
				currentSubscriberService.getSubscriberId(), vList, dp.getDateFrom(), dp.getDateTo());

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectContObjectsNotificationsNewCountList() throws Exception {

		List<Long> vList = subscrContObjectService.selectSubscriberContObjectIds(subscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository.selectNotificatoinsCountList(subscriberId(), vList,
				dp.getDateFrom(), dp.getDateTo(), Boolean.TRUE);

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	@Test
	public void testSelectContObjectsContTypes() throws Exception {
		List<Long> vList = subscrContObjectService.selectSubscriberContObjectIds(subscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<Object[]> resultList = subscrContEventNotificationRepository
				.selectNotificationEventTypeCountGroup(subscriberId(), vList, dp.getDateFrom(), dp.getDateTo());

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());

		for (Object[] oo : resultList) {
			logger.info("oo[0]:{} {}, oo[1]:{} {}", oo[0], oo[0].getClass().getName(), oo[1],
					oo[1].getClass().getName());

		}

	}

	@Test
	public void testSelectContObjectsContTypesCollapse() throws Exception {
		List<Long> vList = subscrContObjectService.selectSubscriberContObjectIds(subscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<Object[]> resultList = subscrContEventNotificationRepository
				.selectNotificationEventTypeCountGroupCollapse(subscriberId(), vList, dp.getDateFrom(), dp.getDateTo());

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());

		for (Object[] oo : resultList) {
			logger.info("oo[0]:{} (__{}__), oo[1]:{} (__{}__)", oo[0], oo[0].getClass().getSimpleName(), oo[1],
					oo[1].getClass().getSimpleName());

		}

	}
}
