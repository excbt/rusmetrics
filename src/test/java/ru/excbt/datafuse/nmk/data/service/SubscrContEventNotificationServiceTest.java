package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
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
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;

public class SubscrContEventNotificationServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContEventNotificationServiceTest.class);

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContEventService contEventService;

	@Autowired
	private CurrentUserService currentUserService;

	@Test
	public void testFindAll() {
		Page<?> result = subscrContEventNotifiicationService.selectAll(
				currentSubscriberService.getSubscriberId(), true, null);
		assertNotNull(result);
	}

	@Test
	public void testFindByDates() {

		Date fromDate = DateTime.now().minusDays(10).toDate();
		Date toDate = DateTime.now().toDate();

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotifiicationService.AVAILABLE_SORT_FIELDS[0]);

		List<Long> contObjectList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());

		List<Long> contEventTypeIdList = contEventService
				.findAllContEventTypes().stream().map(cet -> cet.getId())
				.collect(Collectors.toList());

		Page<?> result = subscrContEventNotifiicationService
				.selectByConditions(currentSubscriberService.getSubscriberId(),
						fromDate, toDate, contObjectList, contEventTypeIdList,
						null, request);

		assertNotNull(result);
	}

	@Test
	public void testUpdateIsNew() {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotifiicationService.AVAILABLE_SORT_FIELDS[0]);

		Page<SubscrContEventNotification> canidate = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), true,
						request);

		assertNotNull(canidate);
		List<SubscrContEventNotification> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId())
				.collect(Collectors.toList());

		logger.info("Current User Id:{}", currentUserService.getCurrentUserId());
		
		subscrContEventNotifiicationService.updateIsNew(updateIds,
				currentUserService.getCurrentUserId());

		SubscrContEventNotification result = subscrContEventNotifiicationService
				.findOne(updateIds.get(0));

		logger.info("Update Result. id:{} isNew:{}", result.getId(),
				result.getIsNew());

	}

}
