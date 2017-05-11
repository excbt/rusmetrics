package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

@Transactional
public class SubscrUserControllerTest extends RmaControllerTest {

	@Autowired
	private SubscrUserService subscrUserService;

	@Test
	public void testGetSubscrUsers() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/subscrUsers"));
	}

	@Test
	public void testSubscrUserCRUD() throws Exception {
		SubscrUser subscrUser = new SubscrUser();
		String username = "usr_" + System.currentTimeMillis();
		subscrUser.setUserName(username);
		subscrUser.setUserNickname("user_" + username + "_FN");

		RequestExtraInitializer paramAdmin = (builder) -> {
			builder.param("isAdmin", "true");
			builder.param("newPassword", "secret");
		};

		Long subscrUserId = _testCreateJson(UrlUtils.apiSubscrUrl("/subscrUsers"), subscrUser, paramAdmin);
		subscrUser = subscrUserService.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");

		RequestExtraInitializer paramUpd = (builder) -> {
			builder.param("isAdmin", "false");
			builder.param("oldPassword", "secret");
			builder.param("newPassword", "secret2");
		};

		_testUpdateJson(UrlUtils.apiSubscrUrl("/subscrUsers", subscrUserId), subscrUser, paramUpd);

		RequestExtraInitializer param = (builder) -> {
			builder.param("isPermanent", "true");
		};

		_testDeleteJson(UrlUtils.apiSubscrUrl("/subscrUsers", subscrUserId), param);
	}
}
