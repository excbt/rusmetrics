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
public class RmaSubscrUserControllerTest extends RmaControllerTest {

	private final static String RMA_RSUBSCRIBER_URL = "/" + 64166467 + "/subscrUsers";

	@Autowired
	private SubscrUserService subscrUserService;

	@Test
	public void testSubscrUsersGet() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl(RMA_RSUBSCRIBER_URL));
	}

	@Test
	public void testRSubscrUserCRUD() throws Exception {

		SubscrUser subscrUser = new SubscrUser();

		String username = "usr_" + System.currentTimeMillis();
		subscrUser.setUserName(username);
		subscrUser.setUserNickname("user_" + username + "_FN");

		RequestExtraInitializer paramAdmin = (builder) -> {
			builder.param("isAdmin", "true");
			builder.param("newPassword", "secret");
		};

		Long subscrUserId = _testCreateJson(UrlUtils.apiRmaUrl(RMA_RSUBSCRIBER_URL), subscrUser, paramAdmin);
		subscrUser = subscrUserService.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");
		_testUpdateJson(UrlUtils.apiRmaUrl(RMA_RSUBSCRIBER_URL, subscrUserId), subscrUser);

		RequestExtraInitializer param = (builder) -> {
			builder.param("isPermanent", "true");
		};

		_testDeleteJson(UrlUtils.apiRmaUrl(RMA_RSUBSCRIBER_URL, subscrUserId), param);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCheckUsername() throws Exception {

		RequestExtraInitializer param = (builder) -> {
			builder.param("username", "admin");
		};

		_testGet(UrlUtils.apiRmaUrl("/subscrUsers/checkExists"), param);
	}
}
