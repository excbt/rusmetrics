package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrContEventTest extends AnyControllerTest {

	@Test
	@Ignore
	public void testSubscrContObjectEvents() throws Exception {
		testJsonGet("/api/subscr/contObjects/events");
	}

	@Test
	public void testSubscrContObjectEventsFilter() throws Exception {

		ResultActions resultActionsAll = mockMvc.perform(get(
				"/api/subscr/contObjects/eventsFilter").with(
				testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	@Test
	public void testSubscrContObjectEventsFilterDate() throws Exception {

		ResultActions resultActionsAll = mockMvc.perform(get(
				"/api/subscr/contObjects/eventsFilter")
				.param("startDate", "2015-04-01")
				.param("endDate", "2015-05-01").with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}

	@Test
	public void testSubscrContObjectEventsFilterDateContObjects()
			throws Exception {

		ResultActions resultActionsAll = mockMvc
				.perform(get("/api/subscr/contObjects/eventsFilter")
						.param("startDate", "2015-04-01")
						.param("endDate", "2015-05-01")
						.param("contObjectIds", "20118671")
						.with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
	}


	@Test
	@Ignore
	public void testSubscrContObjectEventsPaged() throws Exception {
		testJsonGet ("/api/subscr/contObjects/events/paged");
	}
	
	
}
