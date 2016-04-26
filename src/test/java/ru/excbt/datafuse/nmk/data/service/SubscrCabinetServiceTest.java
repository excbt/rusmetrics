package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public class SubscrCabinetServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetServiceTest.class);

	@Autowired
	private SubscrCabinetService subscrCabinetService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testName() throws Exception {

		Subscriber subsciber = subscriberService.findOne(getSubscriberId());
		assertNotNull(subsciber);

		subscrCabinetService.createSubscrUserCabinet(subsciber, new Long[] { 29863789L });
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscriberId() {
		return 512156297L;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscrUserId() {
		return 512156325L;
	}

}
