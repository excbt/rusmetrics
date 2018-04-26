package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import com.google.common.primitives.Longs;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrActionControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrActionControllerTest.class);

	@Autowired
	private SubscrActionUserService subscrActionUserService;

	@Autowired
	private SubscrActionGroupService subscrActionGroupService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private SubscrActionController subscrActionController;
    @Autowired
	private SubscrActionUserGroupService subscrActionUserGroupService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrActionController = new SubscrActionController(subscrActionGroupService, subscrActionUserService, subscrActionUserGroupService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrActionController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetGroup() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/subscrAction/groups").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetUser() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/subscrAction/users").testGet();
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteUser() throws Exception {

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
                    TestUtils.arrayToString(Longs.toArray(groupIds)));

			}
		};

        Long createdId = mockMvcRestWrapper.restRequest( "/api/subscr/subscrAction/users")
            .requestBuilder(b -> b.param("subscrGroupIds",
                TestUtils.arrayToString(Longs.toArray(groupIds))))
            .testPost(user).getLastId();

		assertTrue(createdId > 0);

        mockMvcRestWrapper.restRequest("/api/subscr/subscrAction/users/{id}", createdId).testDelete();

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteGroup() throws Exception {
		List<SubscrActionUser> userList = subscrActionUserService
				.findAll(currentSubscriberService.getSubscriberId());
		List<Long> userIds = new ArrayList<Long>();
		for (SubscrActionUser s : userList) {
			userIds.add(s.getId());
		}

		SubscrActionGroup grp = new SubscrActionGroup();
		grp.setGroupName("Group 111");
		grp.setGroupComment("created by rest");

        Long createdId = mockMvcRestWrapper.restRequest("/api/subscr/subscrAction/groups")
            .requestBuilder(b -> b.param("subscrUserIds", TestUtils.arrayToString(Longs.toArray(userIds))))
            .testPost(grp).getLastId();

        mockMvcRestWrapper.restRequest("/api/subscr/subscrAction/groups/{id}", createdId).testDelete();
	}

}
