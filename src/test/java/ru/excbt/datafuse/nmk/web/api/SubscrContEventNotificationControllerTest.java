package ru.excbt.datafuse.nmk.web.api;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorV2;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorV2Repository;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class SubscrContEventNotificationControllerTest extends AnyControllerTest {

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

		RequestExtraInitializer params = (b) -> {
			b.param("fromDate", "2015-06-01").param("toDate", "2015-06-30");
			b.param("contObjectIds", TestUtils.listToString(contObjectList));
			b.param("contEventTypeIds", TestUtils.listToString(contEventTypeIdList));
			b.param("page", "0").param("size", "100");
			b.param("sortDesc", "false");
			b.param("contEventCategories", TestUtils.listToString(contEventTypeCategoryList));
		};

		_testGetJson("/api/subscr/contEvent/notifications/paged", params);

	}

	@Test
    @Transactional
	public void testNotificationRevisionIsNew() throws Exception {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotificationService.AVAILABLE_SORT_FIELDS[0]);

		Page<SubscrContEventNotification> canidate = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), true, request);

		assertNotNull(canidate);
		List<SubscrContEventNotification> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId()).collect(Collectors.toList());

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {
			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("notificationIds", TestUtils.listToString(updateIds));
			}
		};

		_testUpdateJson("/api/subscr/contEvent/notifications/revision", null, extraInitializer);

		_testUpdateJson("/api/subscr/contEvent/notifications/revision/isNew", null, extraInitializer);

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

		ResultActions resultActionsAll = mockMvc
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

		ResultActions resultActionsAll = mockMvc.perform(
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

		RequestExtraInitializer params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse", params);
	}

	@Test
    @Transactional
	public void testCityMonitorContObjectCityStatusCollapseV2() throws Exception {

		RequestExtraInitializer params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapseV2", params);
	}

	@Test
    @Transactional
	public void testCityMonitorContObjectCityStatusCollapseGrouped() throws Exception {

		RequestExtraInitializer params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
			builder.param("contGroupId", "488528511");
		};

		_testGetJson("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse", params);

	}

	@Ignore
	@Test
    @Transactional
	public void testNotificationsContObjectEventTypes() throws Exception {

		long contObjectId = 20118695;

		RequestExtraInitializer params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

		_testGetJson(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes", contObjectId),
				params);

	}

	@Test
    @Transactional
	public void testNotificationsContObjectEventTypesStatusCollapse() throws Exception {

		long contObjectId = 20118695;

		RequestExtraInitializer params = (builder) -> {
			builder.param("fromDate", "2015-06-01");
			builder.param("toDate", "2015-06-30");
		};

		_testGetJson(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes/statusCollapse",
				contObjectId), params);

	}

	@Test
    @Transactional
	public void testNotificationsContObjectMonitor() throws Exception {
		long contObjectId = 20118695;
		String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEvents", contObjectId);

		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testNotificationsContObjectMonitorV2() throws Exception {
		long contObjectId = 75224930;
		String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEventsV2", contObjectId);

		_testGetJson(url);
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
        contEventMonitorV2.setContEventTime(contEvent.getEventTime());
        contEventMonitorV2Repository.saveAndFlush(contEventMonitorV2);


        String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEventsV2/byContZPoint/%d", contObjectId, contZPointId);

        _testGetJson(url);
    }

	@Test
    @Transactional
	public void testNotificationsMonitorColor() throws Exception {
		String url = "/api/subscr/contEvent/notifications/monitorColor";

		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testContEventCategories() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/contEvent/categories");
		_testGetJson(url);
	}

	@Test
    @Transactional
	public void testContEventDeviation() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/contEvent/deviations");
		_testGetJson(url);
	}

	@Override
	public long getSubscriberId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
