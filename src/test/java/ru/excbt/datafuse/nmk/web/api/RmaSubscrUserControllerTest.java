package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class RmaSubscrUserControllerTest extends PortalApiTest {

	public static final int TEST_RMA_ID = 64166467;

	@Autowired
	private SubscrUserService subscrUserService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private RmaSubscrUserController rmaSubscrUserController;

	@Autowired
    private SubscrRoleService subscrRoleService;
    @Autowired
	private SubscriberService subscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaSubscrUserController = new RmaSubscrUserController(subscrUserService, subscrRoleService, portalUserIdsService, subscriberService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscrUserController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
	public void testSubscrUsersGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrUsers", TEST_RMA_ID).testGet();
	}

	@Test
	public void testRSubscrUserCRUD() throws Exception {

		SubscrUser subscrUser = new SubscrUser();

		String username = "usr_" + System.currentTimeMillis();
		subscrUser.setUserName(username);
		subscrUser.setUserNickname("user_" + username + "_FN");

        Long subscrUserId = mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrUsers", TEST_RMA_ID)
            .requestBuilder(b -> b
                .param("isAdmin", "true")
                .param("newPassword", "secret"))
            .testPost(subscrUser)
            .getLastId();

		subscrUser = subscrUserService.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");
        mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrUsers/{id2}", TEST_RMA_ID, subscrUserId).testPut(subscrUser);

		mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrUsers/{id2}", TEST_RMA_ID, subscrUserId)
            .requestBuilder(b -> b.param("isPermanent", "true"))
            .testDelete();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCheckUsername() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscrUsers/checkExists")
            .requestBuilder(b -> b.param("username", "admin"))
            .testGet();
	}
}
