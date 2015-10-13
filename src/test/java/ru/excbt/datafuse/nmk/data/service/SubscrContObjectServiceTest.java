package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class SubscrContObjectServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrContObjectIds() throws Exception {
		List<Long> contObjectIds = subscrContObjectService
				.selectRmaSubscrContObjectIds(currentSubscriberService.getSubscriberId());
		assertTrue(contObjectIds.size() > 0);
	}
}
