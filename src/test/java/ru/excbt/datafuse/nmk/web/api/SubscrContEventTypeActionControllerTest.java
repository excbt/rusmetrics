package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeActionService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.SubscrControllerTest;

public class SubscrContEventTypeActionControllerTest extends SubscrControllerTest implements TestExcbtRmaIds {

	@Autowired
	private SubscrContEventTypeActionService subscrContEventTypeActionService;

	@Autowired
	private SubscrActionUserService subscrActionUserService;

	@Test
	public void testAvailableContEventTypes() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/contEventType/actions/available"));
	}

	@Test
	public void testUpdateEventTypeAction() throws Exception {
		List<ContEventType> availList = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availList.size() > 0);
		Long contEventTypeId = availList.get(0).getId();

		List<SubscrActionUser> actionUsers = subscrActionUserService.findAll(EXCBT_RMA_SUBSCRIBER_ID);

		assertTrue(actionUsers.size() > 1);

		SubscrContEventTypeAction newUserAction = new SubscrContEventTypeAction();
		newUserAction.setIsEmail(false);
		newUserAction.setIsSms(true);
		newUserAction.setSubscrActionUserId(actionUsers.get(0).getId());

		SubscrContEventTypeAction newUserAction2 = new SubscrContEventTypeAction();
		newUserAction2.setIsEmail(true);
		newUserAction2.setIsSms(true);
		newUserAction2.setSubscrActionUserId(actionUsers.get(2).getId());

		String url = String.format("/api/subscr/contEventType/%d/actions", contEventTypeId);

		_testUpdateJson(url, Arrays.asList(newUserAction, newUserAction2));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testContEventTypeAction() throws Exception {
		List<ContEventType> availList = subscrContEventTypeActionService.selectAvailableContEventTypes();
		assertTrue(availList.size() > 0);
		Long contEventTypeId = availList.get(0).getId();

		String url = String.format("/api/subscr/contEventType/%d/actions", contEventTypeId);

		_testGetJson(url);
	}

}
