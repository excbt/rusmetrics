package ru.excbt.datafuse.nmk.web.api;

import java.util.Date;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

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
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrVCookieService;
import ru.excbt.datafuse.nmk.data.service.WidgetMetaService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrVCookieControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrVCookieController subscrVCookieController;
    @Autowired
    private WidgetMetaService widgetMetaService;
    @Autowired
    private SubscrVCookieService subscrVCoockieService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrVCookieController = new SubscrVCookieController(subscrVCoockieService, widgetMetaService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrVCookieController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	public void testCRUDSubscrVCookie() throws Exception {

		MeasureUnit entity = new MeasureUnit();
		entity.setKeyname("TEST");
		entity.setMeasureCategory("CAT");
		entity.setUnitName("My name is TEST");

		SubscrVCookie vc = new SubscrVCookie();
		vc.setVcKey("TEST_MU");
		vc.setVcMode("TEST_MODE");
		vc.setVcValue(TestUtils.objectToJsonStr(entity));

        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/list").testPut(Lists.newArrayList(vc));

		vc.setDevComment("Override at " + new Date());

        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/list").testPut(Lists.newArrayList(vc));

	}

	@Test
	public void testGetSubscrVCookie() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/vcookie").testGet();
//		_testGetJson("/api/subscr/vcookie");
	}

	@Test
	public void testCRUDSubscrUserVCookie() throws Exception {

		MeasureUnit entity = new MeasureUnit();
		entity.setKeyname("TEST");
		entity.setMeasureCategory("CAT");
		entity.setUnitName("My name is TEST");

		SubscrVCookie vc = new SubscrVCookie();
		vc.setVcKey("TEST_MU");
		vc.setVcMode("TEST_MODE");
		vc.setVcValue(TestUtils.objectToJsonStr(entity));

        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/user/list").testPut(Lists.newArrayList(vc));

		vc.setDevComment("Override at " + new Date());

        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/user/list").testPut(Lists.newArrayList(vc));

	}

	@Test
	public void testGetSubscrUserVCookie() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/user").testGet();

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("vcMode", "TEST_MODE");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/user")
            .requestBuilder(params).testGet();
	}

	@Test
	public void testWidgetsList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/vcookie/widgets/list").testGet();
	}

}
