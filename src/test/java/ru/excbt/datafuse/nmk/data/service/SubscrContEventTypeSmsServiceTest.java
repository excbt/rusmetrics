package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrContEventTypeSmsServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventTypeSmsServiceTest.class);

	@Autowired
	private SubscrContEventTypeSmsService subscrContEventTypeSmsService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testAvailableContEventTypes() throws Exception {
		List<ContEventType> availTypes = subscrContEventTypeSmsService.selectAvailableContEventTypes();
		assertTrue(availTypes.size() > 0);
		logger.info("Found {} contEventTypes for sms", availTypes.size());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCreateSubscrContEventTypeSms() throws Exception {
		Subscriber subscriber = subscriberService.findOne(EXCBT_RMA_SUBSCRIBER_ID);
		List<ContEventType> availTypes = subscrContEventTypeSmsService.selectAvailableContEventTypes();
		assertTrue(availTypes.size() > 0);
		ContEventType contEventType = availTypes.get(0);

		SubscrContEventTypeSmsAddr smsAddr = new SubscrContEventTypeSmsAddr();
		smsAddr.setAddressSms("+7(123) 456-78-90");

		SubscrContEventTypeSms resultSms = subscrContEventTypeSmsService.createSubscrContEventTypeSms(subscriber,
				contEventType, Arrays.asList(smsAddr));
		subscrContEventTypeSmsService.deleteSubscrContEventTypeSms(resultSms.getId());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeleteSubscrContEventTypeSms() throws Exception {
		subscrContEventTypeSmsService.deleteSubscrContEventTypeSms(220841099L);
	}

}
