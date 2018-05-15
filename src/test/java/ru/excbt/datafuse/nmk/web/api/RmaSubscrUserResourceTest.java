package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
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
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.service.SubscrUserManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.RmaSubscrUserResource;
import ru.excbt.datafuse.nmk.web.rest.SubscrUserResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class RmaSubscrUserResourceTest extends PortalApiTest {

	public static final int TEST_RMA_ID = 64166467;

	@Autowired
	private SubscrUserService subscrUserService;

    @Autowired
    private SubscrUserRepository subscrUserRepository;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private RmaSubscrUserResource rmaSubscrUserResource;

	@Autowired
    private SubscrRoleService subscrRoleService;

    @Autowired
	private SubscriberService subscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;
    @Autowired
    private SubscrUserMapper subscrUserMapper;
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private SubscrUserManageService subscrUserManageService;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaSubscrUserResource = new RmaSubscrUserResource(subscrUserService, subscrRoleService, portalUserIdsService, subscriberRepository, subscrUserMapper, subscrUserManageService, subscriberService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscrUserResource)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
	public void testSubscrUsersGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrUsers", TEST_RMA_ID).testGet();
	}

    /**
     * TODO FIX user creation
     * @throws Exception
     */
	@Test
    @Ignore
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

		subscrUser = subscrUserRepository.findOne(subscrUserId);
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
