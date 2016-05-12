package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrPrefServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPrefServiceTest.class);

	@Autowired
	private SubscrPrefService subscrPrefService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefByType() throws Exception {
		List<SubscrPref> subscrPrefList = subscrPrefService.selectSubscrPrefsBySubscrType("RMA");
		assertNotNull(subscrPrefList);
		assertTrue(subscrPrefList.size() > 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubscriberPrefValue() throws Exception {

		List<SubscrPrefValue> values = subscrPrefService.selectSubscrPrefValue(getSubscriberParam());
		assertTrue(values.size() > 0);
		for (SubscrPrefValue subscrPrefValue : values) {
			logger.info("SubscrPref: {}", subscrPrefValue.getSubscrPrefKeyname());
		}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
