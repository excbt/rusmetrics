package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaSubscrUserControllerTest extends RmaControllerTest {

	private final static String RMA_RSUBSCRIBER_URL = "/" + 64166467 + "/subscrUsers";

	@Autowired
	private SubscrUserService subscrUserService;

	@Test
	public void testSubscrUsersGet() throws Exception {
		_testJsonGet(apiRmaUrl(RMA_RSUBSCRIBER_URL));
	}

	@Test
	public void testRSubscrUserCRUD() throws Exception {
		SubscrUser subscrUser = new SubscrUser();
		subscrUser.setUserName("usr_" + System.currentTimeMillis());

		RequestExtraInitializer paramAdmin = (builder) -> {
			builder.param("isAdmin", "true");
		};

		Long subscrUserId = _testJsonCreate(apiRmaUrl(RMA_RSUBSCRIBER_URL), subscrUser, paramAdmin);
		subscrUser = subscrUserService.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");
		_testJsonUpdate(apiRmaUrl(RMA_RSUBSCRIBER_URL, subscrUserId), subscrUser);

		RequestExtraInitializer param = (builder) -> {
			builder.param("isPermanent", "true");
		};

		_testJsonDelete(apiRmaUrl(RMA_RSUBSCRIBER_URL, subscrUserId), param);
	}

}
