package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class SubscrUserControllerTest extends RmaControllerTest {

	@Autowired
	private SubscrUserService subscrUserService;

	@Test
	public void testGetSubscrUsers() throws Exception {
		_testJsonGet(apiSubscrUrl("/subscrUsers"));
	}

	@Test
	public void testSubscrUserCRUD() throws Exception {
		SubscrUser subscrUser = new SubscrUser();
		subscrUser.setUserName("usr_" + System.currentTimeMillis());

		RequestExtraInitializer paramAdmin = (builder) -> {
			builder.param("isAdmin", "true");
		};

		Long subscrUserId = _testJsonCreate(apiSubscrUrl("/subscrUsers"), subscrUser, paramAdmin);
		subscrUser = subscrUserService.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");
		_testJsonUpdate(apiSubscrUrl("/subscrUsers", subscrUserId), subscrUser);

		RequestExtraInitializer param = (builder) -> {
			builder.param("isPermanent", "true");
		};

		_testJsonDelete(apiSubscrUrl("/subscrUsers", subscrUserId), param);
	}
}
