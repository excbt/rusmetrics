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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContEventNotificationDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorV2Repository;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContEventNotificationMapper;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.TestUtil;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class SubscrContEventNotificationControllerTest extends PortalApiTest {

	private static final Logger log = LoggerFactory.getLogger(SubscrContEventNotificationControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	@Autowired
	private SubscrContEventNotificationService subscrContEventNotifiicationService;

    @Autowired
	private ContZPointService contZPointService;

    @Autowired
    private ContEventRepository contEventRepository;

    @Autowired
    private ContEventMonitorV2Repository contEventMonitorV2Repository;

    @Autowired
    private ObjectAccessService objectAccessService;


    @Autowired
    private SubscrContEventNotificationMapper mapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @MockBean
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrContEventNotificationController subscrContEventNotificationController;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContEventNotificationController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


    /**
     * @return
     */
    private List<Long> findSubscriberContObjectIds() {
        log.debug("Finding objects for subscriberId:{}", getSubscriberId());
        List<Long> result = objectAccessService.findContObjectIds(getSubscriberId());
        assertFalse(result.isEmpty());
        return result;
    }

	@Test
    @Transactional
	public void testNotificationGet() throws Exception {

		List<Long> contObjectList = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());

		List<Long> contEventTypeIdList = contEventTypeService.selectBaseContEventTypes().stream()
				.map(cet -> cet.getId()).collect(Collectors.toList());

		List<String> contEventTypeCategoryList = Arrays.asList("LEAK_WARNING");

        Consumer<MockHttpServletRequestBuilder> params = (b) -> {
			b.param("fromDate", "2015-06-01").param("toDate", "2015-06-30");
			b.param("contObjectIds", TestUtils.listToString(contObjectList));
			b.param("contEventTypeIds", TestUtils.listToString(contEventTypeIdList));
			b.param("page", "0").param("size", "100");
			b.param("sortDesc", "false");
			b.param("contEventCategories", TestUtils.listToString(contEventTypeCategoryList));
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/paged")
            .requestBuilder(params)
            .testGet();

//		_testGetJson("/api/subscr/contEvent/notifications/paged", params);

	}

	@Test
    @Transactional
	public void testNotificationGetContServiceTypes() throws Exception {

		List<Long> contObjectList = objectAccessService.findContObjectIds(currentSubscriberService.getSubscriberId());

		List<Long> contEventTypeIdList = contEventTypeService.selectBaseContEventTypes().stream()
				.map(cet -> cet.getId()).collect(Collectors.toList());

		List<String> contEventTypeCategoryList = Arrays.asList("LEAK_WARNING");
		List<String> contServiceTypes = new ArrayList<>();
        EnumSet.allOf(ContServiceTypeKey.class).forEach(i -> {
            contServiceTypes.add(i.getKeyname());
        });

        Consumer<MockHttpServletRequestBuilder> params = (b) -> {
			b.param("fromDate", "2015-06-01").param("toDate", "2015-06-30");
			b.param("contObjectIds", TestUtils.listToString(contObjectList));
			b.param("contEventTypeIds", TestUtils.listToString(contEventTypeIdList));
			b.param("page", "0").param("size", "100");
			b.param("sortDesc", "false");
			b.param("contEventCategories", TestUtils.listToString(contEventTypeCategoryList));
			b.param("contServiceTypes", TestUtils.listToString(contServiceTypes));
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/paged")
            .requestBuilder(params)
            .testGet();

//		_testGetJson("/api/subscr/contEvent/notifications/paged", params);

	}

	@Test
    @Transactional
	public void testNotificationRevisionIsNew() throws Exception {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotificationService.AVAILABLE_SORT_FIELDS[0]);

		Page<SubscrContEventNotificationDTO> canidate = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), true, request).map(mapper::toDto);

		assertNotNull(canidate);
		List<SubscrContEventNotificationDTO> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId()).collect(Collectors.toList());

        Consumer<MockHttpServletRequestBuilder> params = builder -> builder.param("notificationIds", TestUtils.listToString(updateIds));

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/revision")
            .requestBuilder(params)
            .testPut();

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/revision/isNew")
            .requestBuilder(params)
            .testPut();

//		_testUpdateJson("/api/subscr/contEvent/notifications/revision", null, params);

//		_testUpdateJson("/api/subscr/contEvent/notifications/revision/isNew", null, params);

	}

	/**
	 * Unused method. // Comment date 11.05.2016
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
    @Transactional
	public void testNotificationsContObject() throws Exception {

		ResultActions resultActionsAll = restPortalMockMvc
				.perform(get("/api/subscr/contEvent/notifications/contObject").param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(ApiConst.APPLICATION_JSON_UTF8));
	}

	/**
	 * Unused method. // Comment date 11.05.2016
	 *
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
    @Transactional
	public void testNotificationsContObjectStatusCollapse() throws Exception {

		ResultActions resultActionsAll = restPortalMockMvc.perform(
				get("/api/subscr/contEvent/notifications/contObject/statusCollapse").param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(ApiConst.APPLICATION_JSON_UTF8));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testCityMonitorContObjectCityStatusCollapse() throws Exception {

        Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse")
            .requestBuilder(params)
            .testGet();
//		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse", params);
	}

	@Test
    @Transactional
	public void testCityMonitorContObjectCityStatusCollapseV2() throws Exception {

        Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/contObject/cityStatusCollapseV2")
            .requestBuilder(params)
            .testGet();

//		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapseV2", params);
	}

	@Test
    @Transactional
	public void testCityMonitorContObjectCityStatusCollapseGrouped() throws Exception {

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
			builder.param("contGroupId", "488528511");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse")
            .requestBuilder(params)
            .testGet();
//		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse", params);

	}

	@Ignore
	@Test
    @Transactional
	public void testNotificationsContObjectEventTypes() throws Exception {

		long contObjectId = 20118695;

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/contObject/{id}/eventTypes", contObjectId)
            .requestBuilder(params)
            .testGet();

//		_testGetJson(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes", contObjectId),
//				params);

	}

	@Test
    @Transactional
	public void testNotificationsContObjectEventTypesStatusCollapse() throws Exception {

		long contObjectId = 20118695;

		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

        mockMvcRestWrapper.restRequest("/api/subscr/contEvent/notifications/contObject/{id}/eventTypes/statusCollapse",
            contObjectId)
            .requestBuilder(params)
            .testGet();

//		_testGetJson(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes/statusCollapse",
//				contObjectId), params);

	}

	@Test
    @Transactional
	public void testNotificationsContObjectMonitor() throws Exception {
		long contObjectId = 20118695;
		String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEvents", contObjectId);

        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testNotificationsContObjectMonitorV2() throws Exception {
		long contObjectId = 75224930;
		String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEventsV2", contObjectId);
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

    @Test
    @Transactional
    @Ignore
    public void testNotificationsContObjectMonitorV2ByContZPoint() throws Exception {
        List<Long> contObjectIds = findSubscriberContObjectIds();
        assertFalse(contObjectIds.isEmpty());
        Long contObjectId = contObjectIds.get(0);

        List<Long> contZPointIds = contZPointService.selectContZPointIds(contObjectId);
        assertFalse(contZPointIds.isEmpty());
        Long contZPointId = contZPointIds.get(0);

        List<ContEventType> contEventTypes = contEventTypeService.selectBaseContEventTypes();
        Optional<ContEventType> testEventType = Optional.of(contEventTypes.get(0));
            //contEventTypes.stream().filter(i -> i.getId().equals(740)).findFirst();
        assertTrue(testEventType.isPresent());

        ContEvent contEvent = new ContEvent();
        contEvent.setContEventTypeId(contEvent.getId());
        contEvent.setContObjectId(contObjectId);
        contEvent.setContZPointId(contZPointId);
        contEvent.setMessage("Test Event");
        contEvent.setEventTime(LocalDateUtils.asDate(LocalDateTime.now()));
        contEvent.setRegistrationTimeTZ(LocalDateUtils.asDate(LocalDateTime.now()));
        contEventRepository.saveAndFlush(contEvent);

        ContEventMonitorV2 contEventMonitorV2 = new ContEventMonitorV2();
        contEventMonitorV2.setContObjectId(contObjectId);
        contEventMonitorV2.setContEventTypeId(testEventType.get().getId());
        contEventMonitorV2.setContEventId(contEvent.getId());
        contEventMonitorV2.setContEventLevel(1000);
        contEventMonitorV2.setContEventLevelColor(new ContEventLevelColorV2().keyname(ContEventLevelColorKeyV2.RED.keyName()));
        contEventMonitorV2.setContEventTime(LocalDateUtils.asLocalDateTime(contEvent.getEventTime()));
        contEventMonitorV2Repository.saveAndFlush(contEventMonitorV2);


        String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEventsV2/byContZPoint/%d", contObjectId, contZPointId);

        mockMvcRestWrapper.restRequest(url).testGet();
//        _testGetJson(url);
    }

	@Test
    @Transactional
	public void testNotificationsMonitorColor() throws Exception {
		String url = "/api/subscr/contEvent/notifications/monitorColor";
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testContEventCategories() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/contEvent/categories");
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testContEventDeviation() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/contEvent/deviations");
        mockMvcRestWrapper.restRequest(url).testGet();
//		_testGetJson(url);
	}

	public long getSubscriberId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
	}

	/**
	 *
	 * @return
	 */
	public long getSubscrUserId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
