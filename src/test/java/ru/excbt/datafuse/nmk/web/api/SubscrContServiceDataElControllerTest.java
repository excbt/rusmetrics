package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataElService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.function.Consumer;

@RunWith(SpringRunner.class)
public class SubscrContServiceDataElControllerTest extends PortalApiTest {

	private final static Long TEST_OBJECT_ID = 66948436L;
	//private final static Long EL_ZPOINT_ID = 159919982L;
	private final static Long EL_ZPOINT_ID = 28063671L; // for ex_srv
	//	private final static Long TEST_OBJECT_ID = 725L;
	//	private final static Long EL_ZPOINT_ID = 183740672L;

	private final static String CONS_TIME_DETAIL = TimeDetailKey.TYPE_ABS.getKeyname();
	private final static String PROFILE_TIME_DETAIL = TimeDetailKey.TYPE_30MIN.getKeyname();
	private final static String TECH_TIME_DETAIL = TimeDetailKey.TYPE_ABS.getKeyname();


	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

    private SubscrContServiceDataElController subscrContServiceDataElController;

    @Autowired
    private ContServiceDataElService contServiceDataElService;

    @Autowired
    private ContZPointService contZPointService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContServiceDataElController = new SubscrContServiceDataElController(contServiceDataElService, contZPointService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContServiceDataElController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}

//	private RequestExtraInitializer requestParamInitializer() {
//		return (builder) -> {
//			builder.param("beginDate", "2015-12-01");
//			builder.param("endDate", "2015-12-31");
//		};
//	}

    private Consumer<MockHttpServletRequestBuilder> dateParams = (builder) -> {
        builder.param("beginDate", "2015-12-01");
        builder.param("endDate", "2015-12-31");
    };

	/**
	 *
	 * @throws Exception
	 */
	@JsonIgnore
	@Test
	public void testElConsSummaryExtSrv() throws Exception {

        mockMvcRestWrapper.restRequest("/api/subscr/{0}/serviceElCons/{1}/{2}/summary", 28063670, "24h", 28063671 )
            .requestBuilder(b -> b.param("beginDate", "2016-03-01").param("endDate", "2016-04-01"))
            .testGet();

//		RequestExtraInitializer params = (builder) -> {
//			builder.param("beginDate", "2016-03-01");
//			builder.param("endDate", "2016-04-01");
//		};
//		;
//
//		String url = UrlUtils.apiSubscrUrl(String.format("/%d/serviceElCons/%s/%d/summary", 28063670, "24h", 28063671));
//
//		_testGet(url, params);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testElConsSummary() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElCons/%s/%d/summary", TEST_OBJECT_ID, CONS_TIME_DETAIL, EL_ZPOINT_ID));


        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();

//		_testGet(url, requestParamInitializer());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testElProfileSummary() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElProfile/%s/%d/summary", TEST_OBJECT_ID, PROFILE_TIME_DETAIL, EL_ZPOINT_ID));

        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testElTechSummary() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElTech/%s/%d/summary", TEST_OBJECT_ID, TECH_TIME_DETAIL, EL_ZPOINT_ID));

        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

	@Test
    @Ignore
	public void testElConsData() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElCons/%s/%d", TEST_OBJECT_ID, CONS_TIME_DETAIL, EL_ZPOINT_ID));

        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

	@Test
    @Ignore
	public void testElConsDataAbs() throws Exception {
		String url = UrlUtils.apiSubscrUrl(String.format("/%d/serviceElCons/24h_abs/%d", TEST_OBJECT_ID, EL_ZPOINT_ID));
        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

	@Test
    @Ignore
	public void testElProfileData() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElProfile/%s/%d", TEST_OBJECT_ID, PROFILE_TIME_DETAIL, EL_ZPOINT_ID));
        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

	@Test
    @Ignore
	public void testElTechData() throws Exception {
		String url = UrlUtils.apiSubscrUrl(
				String.format("/%d/serviceElTech/%s/%d", TEST_OBJECT_ID, TECH_TIME_DETAIL, EL_ZPOINT_ID));
        mockMvcRestWrapper.restRequest(url).requestBuilder(dateParams).testGet();
//		_testGet(url, requestParamInitializer());
	}

}
