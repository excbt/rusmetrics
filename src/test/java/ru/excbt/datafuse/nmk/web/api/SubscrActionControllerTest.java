package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

public class SubscrActionControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrActionControllerTest.class);
	
	@Test
	public void testGetGroup() throws Exception {
		testJsonGet("/api/subscr/subscrAction/groups");
	}

	@Test
	public void testGetUser() throws Exception {
		testJsonGet("/api/subscr/subscrAction/users");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteUser() throws Exception {
		String urlStr = "/api/subscr/subscrAction/users";

		
		SubscrActionUser user = new SubscrActionUser();
		user.setUserName("Vasya");
		user.setUserEmail("email");
		
		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		ResultActions resultAction = mockMvc.perform(post(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);
		assertTrue(createdId > 0);
	
		
		testJsonDelete(urlStr + "/" + createdId);
	}

	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteGroup() throws Exception {
		String urlStr = "/api/subscr/subscrAction/groups";
		
		
		SubscrActionGroup grp = new SubscrActionGroup();
		grp.setGroupName("Group 111");
		grp.setGroupComment("created by rest");
		
		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(grp);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		ResultActions resultAction = mockMvc.perform(post(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));
		
		resultAction.andDo(MockMvcResultHandlers.print());
		
		resultAction.andExpect(status().isCreated());
		
		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);
		assertTrue(createdId > 0);
		
		
		testJsonDelete(urlStr + "/" + createdId);
	}
	
}
