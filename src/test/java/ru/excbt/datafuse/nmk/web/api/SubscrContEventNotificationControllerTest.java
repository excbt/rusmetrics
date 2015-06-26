package ru.excbt.datafuse.nmk.web.api;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

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

	@Test
	public void testNotifiicationsAll() throws Exception {
		testJsonGet("/api/subscr/contEvent/notifications/all");
	}

	@Test
	public void testNotifiicationsPaged() throws Exception {

		List<Long> contObjectList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());

		List<Long> contEventTypeIdList = contEventService
				.findAllContEventTypes().stream().map(cet -> cet.getId())
				.collect(Collectors.toList());

		ResultActions resultActionsAll = mockMvc
				.perform(get("/api/subscr/contEvent/notifications/paged")
						.param("fromDate", "2015-06-01")
						.param("toDate", "2015-06-30")
						.param("contObjectIds", ListToString(contObjectList))
						.param("contEventTypeIds", ListToString(contEventTypeIdList))
						.param("page", "0").param("size", "100").param("sortDesc", "false")
						.with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}
}
