package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubscrContEventNotificationRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationRepositoryTest.class);

	@Autowired
	private SubscrContEventNotificationRepository subscrContEventNotificationRepository;


	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ObjectAccessService objectAccessService;

	private Long subscriberId() {
		return currentSubscriberService.getSubscriberId();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSelectContObjectsNotificationsCountList() throws Exception {

		List<Long> vList = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());
//            subscrContObjectService
				//.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());
		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository.selectContObjectNotificatoinsCountList(
				currentSubscriberService.getSubscriberId(), vList, dp.getDateFrom(), dp.getDateTo());

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testSelectContObjectsNotificationsNewCountList() throws Exception {

		List<Long> vList = objectAccessService.findContObjectIds(subscriberId());

		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository.selectContObjectNotificatoinsCountList(subscriberId(), vList,
				dp.getDateFrom(), dp.getDateTo(), Boolean.TRUE);

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	@Test
    @Ignore
	public void testSelectContObjectsContTypes() throws Exception {
		List<Long> vList = objectAccessService.findContObjectIds(subscriberId());
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
    @Ignore
	public void testSelectContObjectsContTypesCollapse() throws Exception {
		List<Long> vList = objectAccessService.findContObjectIds(subscriberId());
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
