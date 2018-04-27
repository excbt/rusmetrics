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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.SubscrUserResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@Ignore
public class SubscrUserResourceTest extends PortalApiTest {

	@Autowired
	private SubscrUserService subscrUserService;

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

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrUserController = new SubscrUserResource(subscrUserService, subscrRoleService, portalUserIdsService, subscrUserMapper);

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
            .testPost().getLastId();

//            _testCreateJson(UrlUtils.apiSubscrUrl("/subscrUsers"), subscrUser, params);
		subscrUser = subscrUserService.findOne(subscrUserId);
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
}
