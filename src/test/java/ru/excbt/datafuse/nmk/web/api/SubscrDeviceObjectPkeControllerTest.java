package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectPkeService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class SubscrDeviceObjectPkeControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectPkeControllerTest.class);

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private SubscrDeviceObjectPkeController subscrDeviceObjectPkeController;

	@Autowired
	private DeviceObjectPkeService deviceObjectPkeService;
    @Autowired
    private ContZPointService contZPointService;
    @Autowired
    private ObjectAccessService objectAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrDeviceObjectPkeController = new SubscrDeviceObjectPkeController(deviceObjectPkeService, contZPointService, objectAccessService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrDeviceObjectPkeController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectPkeTypes() throws Exception {
		String url = "/api/subscr/deviceObjects/pke/types";
        mockMvcRestWrapper.restRequest("/api/subscr/deviceObjects/pke/types").testGet();
//		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectPkeWarn() throws Exception {
		long deviceObjectId = 447631920;
		String url = String.format("/api/subscr/deviceObjects/pke/%d/warn", deviceObjectId);
		List<String> pkeTypes = Arrays.asList("U23_ABOVE_LIMIT");
		logger.info("URL: {}", url);

//		RequestExtraInitializer params = (b) -> {
//			b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
//			b.param("pkeTypeKeynames", TestUtils.listToString(pkeTypes));
//		};
        mockMvcRestWrapper.restRequest(url).requestBuilder((b) -> {
            b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
            b.param("pkeTypeKeynames", TestUtils.listToString(pkeTypes));
        }).testGet();
//		_testGetJson(url, params);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testContZPointPkeWarn() throws Exception {
		long contZPointId = 447697474;
		String url = String.format("/api/subscr/deviceObjects/pke/byContZPoint/%d/warn", contZPointId);
		List<String> pkeTypes = Arrays.asList("FREQUENCY_BELOW_NORMAL");
		logger.info("URL: {}", url);

//		RequestExtraInitializer params = (b) -> {
//			b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
//			b.param("pkeTypeKeynames", TestUtils.listToString(pkeTypes));
//		};

        mockMvcRestWrapper.restRequest(url).requestBuilder((b) -> {
            b.param("beginDate", "2015-11-26").param("endDate", "2015-11-26");
            b.param("pkeTypeKeynames", TestUtils.listToString(pkeTypes));
        }).testGet();
//		_testGetJson(url, params);
	}

}
