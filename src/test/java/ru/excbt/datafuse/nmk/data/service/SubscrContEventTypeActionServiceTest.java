package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class SubscrContEventTypeActionServiceTest extends JpaSupportTest implements TestExcbtRmaIds {

	@Autowired
	private SubscrContEventTypeActionService subscrContEventTypeActionService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrActionUserService subscrActionUserService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAction() throws Exception {

		Subscriber subscriber = subscriberService.findOne(EXCBT_RMA_SUBSCRIBER_ID);
		List<ContEventType> availTypes = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availTypes.size() > 0);
		ContEventType contEventType = availTypes.get(0);

		List<SubscrActionUser> actionUsers = subscrActionUserService.findAll(EXCBT_RMA_SUBSCRIBER_ID);

		assertTrue(actionUsers.size() > 0);

		SubscrContEventTypeAction newUserAction = new SubscrContEventTypeAction();
		newUserAction.setIsEmail(true);
		newUserAction.setIsSms(true);
		newUserAction.setSubscrActionUserId(actionUsers.get(0).getId());

		List<SubscrContEventTypeAction> resultSms = subscrContEventTypeActionService
				.updateSubscrContEventTypeActions(subscriber, contEventType, Arrays.asList(newUserAction));

		assertTrue(resultSms.size() > 0);
		//		subscrContEventTypeSmsService.deleteSubscrContEventTypeSms(resultSms.getId());

	}

}
