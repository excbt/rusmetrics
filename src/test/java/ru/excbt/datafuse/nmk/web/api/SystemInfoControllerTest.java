package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.dto.ExSystemDto;
import ru.excbt.datafuse.nmk.data.repository.keyname.ExSystemRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SystemInfoControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SystemInfoController systemInfoController;
    @Autowired
    private LdapService ldapService;
    @Autowired
    private SystemParamService systemParamService;
    @Autowired
    private SubscrUserService subscrUserService;
    @Autowired
    private CurrentSubscriberUserDetailsService currentSubscriberUserDetails;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private ExSystemRepository exSystemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CurrentSubscriberService currentSubscriberService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        systemInfoController = new SystemInfoController(
            ldapService,
            systemParamService,
            subscrUserService,
            currentSubscriberUserDetails,
            sessionRegistry,
            exSystemRepository,
            modelMapper,
            currentSubscriberService,
            portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(systemInfoController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
    @Ignore
	public void testFullUserInfo() throws Exception {
        mockMvcRestWrapper.restRequest("/api/systemInfo/fullUserInfo").testGet();
	}

	@Test
	public void testReadOnlyMode() throws Exception {
        mockMvcRestWrapper.restRequest("/api/systemInfo/readOnlyMode").testGet();
	}

	@Test
    @Ignore
	public void testChangePassword() throws Exception {

		String urlStr = "/api/systemInfo/passwordChange";

        mockMvcRestWrapper.restRequest(urlStr).requestBuilder((builder) -> {
            builder.contentType(MediaType.APPLICATION_JSON).param("oldPassword", "admin").param("newPassword",
                "admin1");
        }).testPutEmpty();

        mockMvcRestWrapper.restRequest(urlStr) .requestBuilder((builder) -> {
            builder.contentType(MediaType.APPLICATION_JSON).param("oldPassword", "admin1").param("newPassword",
                "admin");
        }).testPutEmpty();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExSystem() throws Exception {
        String content = mockMvcRestWrapper.restRequest("/api/systemInfo/exSystem").testGet().getStringContent();
		List<ExSystemDto> result = TestUtils.fromJSON(new TypeReference<List<ExSystemDto>>() {
		}, content);
		assertNotNull(result);
	}
}
