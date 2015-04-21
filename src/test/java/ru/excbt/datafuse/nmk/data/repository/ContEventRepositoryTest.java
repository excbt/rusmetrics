package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ContEventRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContEventRepositoryTest.class);

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void test() {
		List<?> events = contEventRepository
				.selectBySubscriberId(currentSubscriberService
						.getSubscriberId());
		assertTrue(events.size() > 0);
		logger.info("Found {} events by subscriberId:{}", events.size(),
				currentSubscriberService.getSubscriberId());
	}

	@Test
	public void testWithIds() {
		List<ContEvent> events = contEventRepository
				.selectBySubscriberIdAndContObjectIds(
						currentSubscriberService.getSubscriberId(),
						Arrays.asList(1L, 2L), new PageRequest(0, 1000));
	}

}
