package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.service.SubscrActionGroupService;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import com.google.common.primitives.Longs;

public class SubscrActionControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrActionControllerTest.class);

	@Autowired
	private SubscrActionUserService subscrActionUserService;

	@Autowired
	private SubscrActionGroupService subscrActionGroupService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetGroup() throws Exception {
		_testGetJson("/api/subscr/subscrAction/groups");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetUser() throws Exception {
		_testGetJson("/api/subscr/subscrAction/users");
	}


	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteUser() throws Exception {

		String urlStr = "/api/subscr/subscrAction/users";

		List<SubscrActionGroup> groupList = subscrActionGroupService
				.findAll(currentSubscriberService.getSubscriberId());
		List<Long> groupIds = new ArrayList<Long>();
		for (SubscrActionGroup s : groupList) {
			groupIds.add(s.getId());
		}

		SubscrActionUser user = new SubscrActionUser();
		user.setUserName("Vasya");
		user.setUserEmail("email");

		RequestExtraInitializer extraInializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("subscrGroupIds",
						arrayToString(Longs.toArray(groupIds)));

			}
		};

		Long createdId = _testCreateJson(urlStr, user, extraInializer);

		assertTrue(createdId > 0);

		_testDeleteJson(urlStr + "/" + createdId);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteGroup() throws Exception {
		String urlStr = "/api/subscr/subscrAction/groups";

		List<SubscrActionUser> userList = subscrActionUserService
				.findAll(currentSubscriberService.getSubscriberId());
		List<Long> userIds = new ArrayList<Long>();
		for (SubscrActionUser s : userList) {
			userIds.add(s.getId());
		}

		SubscrActionGroup grp = new SubscrActionGroup();
		grp.setGroupName("Group 111");
		grp.setGroupComment("created by rest");

		
		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {
			
			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("subscrUserIds", arrayToString(Longs.toArray(userIds)));
			}
		};
		
		Long createdId = _testCreateJson(urlStr, grp, extraInitializer);
		
		_testDeleteJson(urlStr + "/" + createdId);
	}

}
