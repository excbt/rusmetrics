package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.service.ContZPointService.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrContObjectServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectServiceTest.class);

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
		logger.debug("Current Subscriber Id: {}", currentSubscriberService.getSubscriberId());

		List<Long> contObjectIds = subscrContObjectService.selectRmaSubscrContObjectIds(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(contObjectIds.size() > 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContZPointInfo() throws Exception {
		List<ContZPointShortInfo> result = subscrContObjectService.selectSubscriberContZPointShortInfo(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(result.size() > 0);
	}

}
