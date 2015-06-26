package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class SubscrContEventNotificationServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContEventService contEventService;

	@Test
	public void testFindAll() {
		Page<?> result = subscrContEventNotifiicationService
				.selectAll(
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
				.selectByConditions(
						currentSubscriberService.getSubscriberId(), fromDate,
						toDate, contObjectList, contEventTypeIdList, null,
						request);

		assertNotNull(result);
	}

}
