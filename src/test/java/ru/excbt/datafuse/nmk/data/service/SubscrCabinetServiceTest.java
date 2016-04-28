package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.SubscrCabinetInfo;
import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;

public class SubscrCabinetServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetServiceTest.class);

	@Autowired
	private SubscrCabinetService subscrCabinetService;

	@Autowired
	private SubscrUserService subscrUserService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private LdapService ldapService;

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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreate() throws Exception {

		Subscriber subsciber = subscriberService.findOne(getSubscriberId());
		assertNotNull(subsciber);

		SubscrCabinetInfo cabinetInfo = subscrCabinetService.createSubscrUserCabinet(subsciber,
				new Long[] { 29863789L });
		logger.info("Created Subscriber id={}", cabinetInfo.getSubscriber().getId());

	}

	@Test
	public void testDelete() throws Exception {
		List<Subscriber> subscribers = subscriberService.selectChildSubscribers(getSubscriberId());
		assertNotNull(subscribers);
		assertFalse(subscribers.isEmpty());

		for (Subscriber s : subscribers) {
			subscrCabinetService.deleteSubscrUserCabinet(s);
		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContObjectCabinetInfoList() throws Exception {
		List<ContObjectCabinetInfo> result = subscrCabinetService
				.selectSubscrContObjectCabinetInfoList(getSubscriberId());

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

}
