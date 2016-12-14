package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Tuple;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.ContZPointService.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrContObjectServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectServiceTest.class);

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscrContObjectRepository subscrContObjectRepository;

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
		List<ContZPointShortInfo> result = subscrContObjectService
				.selectSubscriberContZPointShortInfo(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(result.size() > 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectContObjectSubscriberIdsByRma() throws Exception {
		List<Long> ids = subscrContObjectService.selectContObjectSubscriberIdsByRma(64166466L, 29863789L);

		ids.forEach(i -> logger.info("id:{}", i));

	}

	@Test
	public void testSelectContObjectShortInfo() throws Exception {
		List<ContObjectShortInfo> result = subscrContObjectService.selectSubscriberContObjectsShortInfo(64166466L);
		result.forEach(i -> logger.info("id:{}", i.getContObjectId()));
	}

	@Test
	public void testSubscrDeviceObjects() throws Exception {

		List<Tuple> resultRows = subscrContObjectRepository
				.selectSubscrDeviceObjectByNumber(getSubscriberParam().getSubscriberId(), Arrays.asList("104115"));

		List<Tuple> resultRows2 = subscrContObjectService.selectSubscriberDeviceObjectByNumber(getSubscriberParam(),
				Arrays.asList("104115"));

		for (Tuple t : resultRows) {
			logger.info("subscriberId: {}", t.get("subscriberId"));

		}

	}

}
