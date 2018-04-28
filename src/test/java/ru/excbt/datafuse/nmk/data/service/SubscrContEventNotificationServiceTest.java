package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.MonitorContEventTypeStatus;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService.SearchConditions;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrContEventNotificationServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationServiceTest.class);

	@Mock
	private PortalUserIdsService portalUserIdsService;


	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private SubscrContEventNotificationService subscrContEventNotifiicationService;

	@Autowired
	private SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService;

	@Autowired
	private ContEventTypeService contEventTypeService;

    @Autowired
	private ObjectAccessService objectAccessService;

	/**
	 *
	 */
	@Test
	public void testFindAll() {
		Page<?> result = subscrContEventNotifiicationService.selectAll(portalUserIdsService.getCurrentIds().getSubscriberId(), true,
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

		List<Long> contObjectList = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());

		List<Long> contEventTypeIdList = contEventTypeService.selectBaseContEventTypes().stream()
				.map(cet -> cet.getId()).collect(Collectors.toList());

		List<String> contEventTypeCategoryList = Arrays.asList("LEAK_WARNING");

		LocalDatePeriod period = LocalDatePeriod.builder().dateFrom(LocalDateTime.now().minusDays(10))
				.dateTo(LocalDateTime.now()).build();

		SearchConditions searchConditions = new SearchConditions(portalUserIdsService.getCurrentIds().getSubscriberId(), period);
		searchConditions.initContObjectIds(contObjectList);
		searchConditions.initContEventTypes(contEventTypeIdList);
		searchConditions.initContEventCategories(contEventTypeCategoryList);

		Page<?> result = subscrContEventNotifiicationService.selectNotificationByConditionsDSL(searchConditions, request);

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
				.selectAll(portalUserIdsService.getCurrentIds().getSubscriberId(), true, request);

		assertNotNull(canidate);
		List<SubscrContEventNotification> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId()).collect(Collectors.toList());

		logger.info("Current User Id:{}", portalUserIdsService.getCurrentIds().getUserId());

		subscrContEventNotifiicationService.updateNotificationRevision(portalUserIdsService.getCurrentIds(), Boolean.FALSE, updateIds);

		SubscrContEventNotification result = subscrContEventNotifiicationService.findNotification(updateIds.get(0));

		logger.info("Update Result. id:{} isNew:{}", result.getId(), result.getIsNew());

		assertTrue(Boolean.FALSE.equals(result.getIsNew()));

		subscrContEventNotifiicationService.updateNotificationRevision(portalUserIdsService.getCurrentIds(), Boolean.TRUE, updateIds);

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
				.selectMonitorContEventNotificationStatus(portalUserIdsService.getCurrentIds().getSubscriberId(), dp);

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
				.selectMonitorContEventTypeStatusCollapse(portalUserIdsService.getCurrentIds(), contObjectId,
						dpp.getLocalDatePeriod());

		assertNotNull(checkList);
		//assertFalse(checkList.isEmpty());

		checkList.forEach((i) -> logger.info("ContEventType: {}. count:{}", i.getContEventType().getKeyname(),
				i.getTotalCount()));

	}

}
