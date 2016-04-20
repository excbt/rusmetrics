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
		List<SubscrPrefValue> values = subscrPrefService.selectSubscrPrefValue(728L);
		assertTrue(values.size() > 0);
		for (SubscrPrefValue subscrPrefValue : values) {
			logger.info("SubscrPref: {}", subscrPrefValue.getSubscrPrefKeyname());
		}
	}

}
