package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
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
import ru.excbt.datafuse.nmk.data.model.support.DatePeriod;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrContEventNotificationRepositoryTest extends JpaSupportTest {

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
	public void testSelectContObjectsNotificationsCountList() throws Exception {

		List<Long> vList = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());
//            subscrContObjectService
				//.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());
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

		List<Long> vList = objectAccessService.findContObjectIds(subscriberId());

		DatePeriod dp = DatePeriod.lastWeek();

		List<?> resultList = subscrContEventNotificationRepository.selectNotificatoinsCountList(subscriberId(), vList,
				dp.getDateFrom(), dp.getDateTo(), Boolean.TRUE);

		assertNotNull(resultList);
		assertTrue(!resultList.isEmpty());
	}

	@Test
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
