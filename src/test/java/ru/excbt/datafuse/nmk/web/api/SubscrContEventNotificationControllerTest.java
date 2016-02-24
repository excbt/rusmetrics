package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

import ru.excbt.datafuse.nmk.data.model.SubscrContEventNotification;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotificationService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrContEventNotificationControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventNotificationControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContEventTypeService contEventTypeService;

	@Autowired
	private SubscrContEventNotificationService subscrContEventNotifiicationService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsGetAll() throws Exception {
		_testGetJson("/api/subscr/contEvent/notifications/all");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsGetPaged() throws Exception {

		List<Long> contObjectList = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());

		List<Long> contEventTypeIdList = contEventTypeService.selectBaseContEventTypes().stream()
				.map(cet -> cet.getId()).collect(Collectors.toList());

		List<String> contEventTypeCategoryList = Arrays.asList("LEAK_WARNING");

		RequestExtraInitializer params = (b) -> {
			b.param("fromDate", "2015-06-01").param("toDate", "2015-06-30");
			b.param("contObjectIds", listToString(contObjectList));
			b.param("contEventTypeIds", listToString(contEventTypeIdList));
			b.param("page", "0").param("size", "100");
			b.param("sortDesc", "false");
			b.param("contEventCategories", listToString(contEventTypeCategoryList));
		};

		_testGetJson("/api/subscr/contEvent/notifications/paged", params);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
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
				builder.param("notificationIds", listToString(updateIds));
			}
		};

		_testUpdateJson("/api/subscr/contEvent/notifications/revision", null, extraInitializer);

		_testUpdateJson("/api/subscr/contEvent/notifications/revision/isNew", null, extraInitializer);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsContObject() throws Exception {

		ResultActions resultActionsAll = mockMvc
				.perform(get("/api/subscr/contEvent/notifications/contObject").param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsContObjectStatusCollapse() throws Exception {

		ResultActions resultActionsAll = mockMvc.perform(
				get("/api/subscr/contEvent/notifications/contObject/statusCollapse").param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsContObjectCityStatusCollapse() throws Exception {

		ResultActions resultActionsAll = mockMvc.perform(
				get("/api/subscr/contEvent/notifications/contObject/cityStatusCollapse").param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotificationsContObjectEventTypes() throws Exception {

		long contObjectId = 20118695;

		ResultActions resultActionsAll = mockMvc.perform(
				get(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes", contObjectId))
						.param("fromDate", "2015-06-01").param("toDate", "2015-06-30").with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotificationsContObjectEventTypesStatusCollapse() throws Exception {

		long contObjectId = 20118695;

		ResultActions resultActionsAll = mockMvc.perform(
				get(String.format("/api/subscr/contEvent/notifications/contObject/%d/eventTypes/statusCollapse",
						contObjectId)).param("fromDate", "2015-06-01").param("toDate", "2015-06-30")
								.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));

	}

	@Test
	public void testNotificationsContObjectMonitor() throws Exception {
		long contObjectId = 20118695;
		String url = String.format("/api/subscr/contEvent/notifications/contObject/%d/monitorEvents", contObjectId);

		_testGetJson(url);
	}

	@Test
	public void testNotificationsMonitorColor() throws Exception {
		String url = "/api/subscr/contEvent/notifications/monitorColor";

		_testGetJson(url);
	}

	@Test
	public void testContEventCategories() throws Exception {
		String url = apiSubscrUrl("/contEvent/categories");
		_testGetJson(url);
	}

	@Test
	public void testContEventCategoryDeviation() throws Exception {
		String url = apiSubscrUrl("/contEvent/categories/RESOURCE_QUALITY/deviations");
		_testGetJson(url);
	}

	@Test
	public void testContEventCategoryDeviationValue() throws Exception {
		String url = apiSubscrUrl("/contEvent/categories/RESOURCE_QUALITY/deviations/RQ_HOT_WATER_TEMP/values");
		_testGetJson(url);
	}
}
