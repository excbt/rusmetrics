package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventNotifiicationService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrContEventNotificationControllerTest extends
		AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContEventNotificationControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContEventService contEventService;

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsAll() throws Exception {
		testJsonGet("/api/subscr/contEvent/notifications/all");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsPaged() throws Exception {

		List<Long> contObjectList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());

		List<Long> contEventTypeIdList = contEventService
				.findAllContEventTypes().stream().map(cet -> cet.getId())
				.collect(Collectors.toList());

		ResultActions resultActionsAll = mockMvc.perform(get(
				"/api/subscr/contEvent/notifications/paged")
				.param("fromDate", "2015-06-01").param("toDate", "2015-06-30")
				.param("contObjectIds", ListToString(contObjectList))
				.param("contEventTypeIds", ListToString(contEventTypeIdList))
				.param("page", "0").param("size", "100")
				.param("sortDesc", "false").with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotificationUpdateIsNew() throws Exception {

		Pageable request = new PageRequest(0, 1, Direction.DESC,
				SubscrContEventNotifiicationService.AVAILABLE_SORT_FIELDS[0]);

		Page<SubscrContEventNotification> canidate = subscrContEventNotifiicationService
				.selectAll(currentSubscriberService.getSubscriberId(), true,
						request);

		assertNotNull(canidate);
		List<SubscrContEventNotification> lst = canidate.getContent();
		assertTrue(lst.size() == 1);

		List<Long> updateIds = lst.stream().map(v -> v.getId())
				.collect(Collectors.toList());

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {
			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("contObjectIds", ListToString(updateIds));
			}
		};

		testJsonUpdate("/api/subscr/contEvent/notifications/revision", null,
				extraInitializer);

		testJsonUpdate("/api/subscr/contEvent/notifications/revision/isNew",
				null, extraInitializer);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotifiicationsContObject() throws Exception {


		ResultActions resultActionsAll = mockMvc
				.perform(get("/api/subscr/contEvent/notifications/contObjects")
						.param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30")
						.with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}
}
