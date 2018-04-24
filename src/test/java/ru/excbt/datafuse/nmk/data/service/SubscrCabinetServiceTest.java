package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.data.model.support.SubscrCabinetInfo;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

@RunWith(SpringRunner.class)
public class SubscrCabinetServiceTest extends PortalDataTest {

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
	public long getSubscriberId() {
		return 512156297L;
	}

	/**
	 *
	 * @return
	 */
	public long getSubscrUserId() {
		return 512156325L;
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateCabinet() throws Exception {

		Subscriber subsciber = subscriberService.selectSubscriber(getSubscriberId());
		assertNotNull(subsciber);

		SubscrCabinetInfo cabinetInfo = subscrCabinetService.createSubscrUserCabinet(subsciber,
				new Long[] { 29863789L });
		logger.info("Created Subscriber id={}", cabinetInfo.getSubscriber().getId());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testDeleteAllCabinets() throws Exception {
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
		//assertFalse(result.isEmpty());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrCabinetContObjectStats() throws Exception {
		boolean result = subscrCabinetService.checkIfSubscriberCabinetsOK(getSubscriberId());

		assertTrue(result);

	}

    @Test
    public void testGetNr() {
	    Long nr = subscrCabinetService.getSubscrCabinetNr();
	    assertNotNull(nr);
    }
}
