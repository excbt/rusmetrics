package ru.excbt.datafuse.nmk.web.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscrUserManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.SubscrUserResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
public class SubscrUserResourceTest extends PortalApiTest {

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

	private SubscrUserResource subscrUserController;
	@Autowired
    private SubscrRoleService subscrRoleService;

    private MockMvcRestWrapper mockMvcRestWrapper;
    @Autowired
    private SubscrUserMapper subscrUserMapper;
    @Autowired
    private SubscrUserManageService subscrUserManageService;
    @Autowired
    private SubscriberService subscriberService;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrUserController = new SubscrUserResource(subscrUserService, subscrRoleService, portalUserIdsService, subscrUserMapper, subscrUserManageService, subscriberService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrUserController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
	public void testGetSubscrUsers() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/subscrUsers").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/subscrUsers"));
	}

	@Test
	public void testSubscrUserCRUD() throws Exception {
		SubscrUser subscrUser = new SubscrUser();
		String username = "usr_" + System.currentTimeMillis();
		subscrUser.setUserName(username);
		subscrUser.setUserNickname("user_" + username + "_FN");

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("isAdmin", "true");
			builder.param("newPassword", "secret");
		};

		Long subscrUserId = mockMvcRestWrapper.restRequest("/api/subscr/subscrUsers")
            .requestBuilder(params)
            .testPost(subscrUser).getLastId();

//            _testCreateJson(UrlUtils.apiSubscrUrl("/subscrUsers"), subscrUser, params);
		subscrUser = subscrUserRepository.findOne(subscrUserId);
		assertNotNull(subscrUser);

		subscrUser.setUserComment("Modified By REST");

//		Consumer<MockHttpServletRequestBuilder> paramUpd = (builder) -> {
//			builder.param("isAdmin", "false");
//			builder.param("oldPassword", "secret");
//			builder.param("newPassword", "secret2");
//		};

        mockMvcRestWrapper.restRequest("/subscrUsers/{id}", subscrUserId)
            .requestBuilder(b -> b
                .param("isAdmin", "false")
                .param("oldPassword", "secret")
                .param("newPassword", "secret2"))
            .testPut(subscrUser);
//		_testUpdateJson(UrlUtils.apiSubscrUrl("/subscrUsers", subscrUserId), subscrUser, paramUpd);

//		RequestExtraInitializer param = (builder) -> {
//			builder.param("isPermanent", "true");
//		};

        mockMvcRestWrapper.restRequest("/subscrUsers")
            .requestBuilder(b -> b.param("isPermanent", "true"))
            .testDelete();
//		_testDeleteJson(UrlUtils.apiSubscrUrl("/subscrUsers", subscrUserId), param);
	}


    @Test
    public void testGetSubscrUsersV2() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-users").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/subscrUsers"));
    }

    @Test
    public void testGetSubscrUsersV2Page() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-users/page").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/subscrUsers"));
    }

    @Test
    public void testGetSubscrUser() throws Exception {
        List<SubscrUser> subscrUsers = subscrUserRepository.selectBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        assertTrue(subscrUsers.size() > 0);
        mockMvcRestWrapper.restRequest("/api/subscr-users/{id}", subscrUsers.get(0).getId()).testGet();

    }

    @Test
    public void testGetCheckSubscrUserExists() throws Exception {
        String username = "admin";
        mockMvcRestWrapper.restRequest("/api/subscr-users/check").requestBuilder(b -> b.param("username", username))
            .testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/subscrUsers"));
    }

    private SubscrUserDTO createTestUser() {
        SubscrUserDTO subscrUserDTO = new SubscrUserDTO();
        String username = "usr_" + System.currentTimeMillis();
        subscrUserDTO.setUserName(username);
        subscrUserDTO.setUserNickname("user_" + username + "_FN");
        subscrUserDTO.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        return subscrUserDTO;
    }

    @Test
    public void testCreateUser() throws Exception {
        SubscrUserDTO subscrUserDTO = createTestUser();

        Long subscrUserId = mockMvcRestWrapper.restRequest("/api/subscr-users")
            .requestBuilder(b -> b.param("newPassword", "my-pass12345"))
            .testPost(subscrUserDTO).getLastId();

        SubscrUser checkUser = subscrUserRepository.findOne(subscrUserId);
        assertThat(checkUser.getUserName(), equalTo(subscrUserDTO.getUserName()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        SubscrUserDTO subscrUserDTO = createTestUser();
        Subscriber s = new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId());
        String initPass = "pass12345";
        Optional<SubscrUser> subscrUserOptional = subscrUserManageService.createSubscrUser(subscrUserDTO, initPass);
        assertThat(subscrUserOptional.isPresent(), is(true));
        subscrUserOptional.ifPresent(d -> subscrUserDTO.setId(d.getId()));
        mockMvcRestWrapper.restRequest("/api/subscr-users")
            .requestBuilder(b -> b.param("newPassword", "my-pass12345").param("oldPassword", initPass))
            .testPut(subscrUserDTO);
    }
}
