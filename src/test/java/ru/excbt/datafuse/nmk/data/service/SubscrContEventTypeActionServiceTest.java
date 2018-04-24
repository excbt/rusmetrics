package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

@RunWith(SpringRunner.class)
public class SubscrContEventTypeActionServiceTest extends PortalDataTest {

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
    @Ignore
	public void testCreateAction() throws Exception {

		Subscriber subscriber = subscriberService.selectSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
		List<ContEventType> availTypes = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availTypes.size() > 0);
		ContEventType contEventType = availTypes.get(0);

		List<SubscrActionUser> actionUsers = subscrActionUserService.findAll(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);

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
