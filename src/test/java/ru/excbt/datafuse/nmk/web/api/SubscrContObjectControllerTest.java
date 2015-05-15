package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrContObjectControllerTest extends AnyControllerTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContObjectControllerTest.class);
	
	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testContObjectGet() throws Exception {
		testJsonGet("/api/subscr/contObjects");
	}

	@Test
	public void testUpdate() throws Exception {
		List<ContObject> subscriberContObject = subscriberService
				.selectSubscriberContObjects(currentSubscriberService
						.getSubscriberId());
		
		assertTrue(subscriberContObject.size() > 0);

		ContObject testCO = subscriberContObject.get(0);
		logger.info("Found ContObject (id={})", testCO.getId());
		testCO.setComment("Updated by REST test at " + DateTime.now().toString());
		
		String urlStr = "/api/subscr/contObjects/" + testCO.getId();
		String jsonBody = OBJECT_MAPPER.writeValueAsString(testCO);
		
		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(put(urlStr)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody).with(testSecurityContext())
					.accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
				
		
		
	}

}
