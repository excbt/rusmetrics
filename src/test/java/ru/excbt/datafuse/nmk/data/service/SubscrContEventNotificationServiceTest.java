package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventTypeStatus;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService.SearchConditions;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class SubscrContEventNotificationServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationServiceTest.class);

	@Autowired
	private SubscrContEventNotificationService subscrContEventNotifiicationService;

	@Autowired
	private SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContEventService contEventService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 */
	@Test
	public void testFindAll() {
		Page<?> result = subscrContEventNotifiicationService.selectAll(currentSubscriberService.getSubscriberId(), true,
				null);
		assertNotNull(result);
	}

	/**
	 * 
	 */
	@Test
	public void testFindByDates() {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotificationService.AVAILABLE_SORT_FIELDS[0]);

		List<Long> contObjectList = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());

		List<Long> contEventTypeIdList = contEventTypeService.selectBaseContEventTypes().stream()
				.map(cet -> cet.getId()).collect(Collectors.toList());

		List<String> contEventTypeCategoryList = Arrays.asList("LEAK_WARNING");

		LocalDatePeriod period = LocalDatePeriod.builder().dateFrom(LocalDateTime.now().minusDays(10))
				.dateTo(LocalDateTime.now()).build();

		SearchConditions searchConditions = new SearchConditions(currentSubscriberService.getSubscriberId(), period);
		searchConditions.initContObjectIds(contObjectList);
		searchConditions.initContEventTypes(contEventTypeIdList);
		searchConditions.initContEventCategories(contEventTypeCategoryList);

		Page<?> result = subscrContEventNotifiicationService.selectNotificationByConditions(searchConditions, request);

		assertNotNull(result);
	}

	/**
	 * 
	 */
	@Test
	public void testUpdateIsNew() {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotificationService.AVAILABLE_SORT_FIELDS[0]);

		Page<SubscrContEventNotification> canidate = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), true, request);

		assertNotNull(canidate);
		List<SubscrContEventNotification> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId()).collect(Collectors.toList());

		logger.info("Current User Id:{}", currentSubscriberService.getCurrentUserId());

		subscrContEventNotifiicationService.updateNotificationsIsNew(Boolean.FALSE, updateIds,
				currentSubscriberService.getCurrentUserId());

		SubscrContEventNotification result = subscrContEventNotifiicationService.findNotification(updateIds.get(0));

		logger.info("Update Result. id:{} isNew:{}", result.getId(), result.getIsNew());

		assertTrue(Boolean.FALSE.equals(result.getIsNew()));

		subscrContEventNotifiicationService.updateNotificationsIsNew(Boolean.TRUE, updateIds,
				currentSubscriberService.getCurrentUserId());

		logger.info("Update Result. id:{} isNew:{}", result.getId(), result.getIsNew());

		result = subscrContEventNotifiicationService.findNotification(updateIds.get(0));

		assertTrue(Boolean.TRUE.equals(result.getIsNew()));
	}

	/**
	 * 
	 */
	@Test
	public void testSubscrContEventNotifications() {
		LocalDatePeriod dp = LocalDatePeriod.lastWeek();

		List<?> list = subscrContEventNotifiicationStatusService
				.selectMonitorContEventNotificationStatus(currentSubscriberService.getSubscriberId(), dp);

		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	//	@Test
	//	public void selectContEventTypeMonitorStatusTest() throws Exception {
	//
	//		long contObjectId = 20118695;
	//		LocalDatePeriodParser dpp = LocalDatePeriodParser.parse("2015-06-01", "2015-06-30");
	//
	//		List<MonitorContEventTypeStatus> checkList = subscrContEventNotifiicationService
	//				.selectMonitorContEventTypeStatus(currentSubscriberService.getSubscriberId(), contObjectId,
	//						dpp.getLocalDatePeriod());
	//
	//		assertNotNull(checkList);
	//		assertFalse(checkList.isEmpty());
	//
	//		checkList.forEach((i) -> logger.info("ContEventType: {}. count:{}", i.getContEventType().getKeyname(),
	//				i.getTotalCount()));
	//
	//	}

	@Test
	public void selectContEventTypeMonitorStatusCollapseTest() throws Exception {

		long contObjectId = 20118695;
		LocalDatePeriodParser dpp = LocalDatePeriodParser.parse("2015-06-01", "2015-06-30");

		List<MonitorContEventTypeStatus> checkList = subscrContEventNotifiicationStatusService
				.selectMonitorContEventTypeStatusCollapse(currentSubscriberService.getSubscriberParam(), contObjectId,
						dpp.getLocalDatePeriod());

		assertNotNull(checkList);
		assertFalse(checkList.isEmpty());

		checkList.forEach((i) -> logger.info("ContEventType: {}. count:{}", i.getContEventType().getKeyname(),
				i.getTotalCount()));

	}

}
