package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

import com.google.common.collect.Lists;

public class ContEventRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContEventRepositoryTest.class);

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testBySubscriberIdLimit() {
		
		long subscriberId = currentSubscriberService
				.getSubscriberId();
		
		Page<ContEvent> events = contEventRepository
				.selectBySubscriber(subscriberId, new PageRequest(0, 1000));
		
		List<ContEvent> list = Lists.newArrayList(events.iterator());
		
		assertTrue(list.size() > 0);
		logger.info("Get {} events by subscriberId:{}", list.size(),
				subscriberId);

		logger.info("Total elements {} by subscriberId:{}", events.getTotalElements(),
				subscriberId);
	}

	@Test
	public void testBySubscriberWithIds() {
		Page<ContEvent> events = contEventRepository
				.selectBySubscriberAndContObjects(
						currentSubscriberService.getSubscriberId(),
						Arrays.asList(1L, 2L), new PageRequest(0, 1000));
		assertNotNull(events);
	}

}
