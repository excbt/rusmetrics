package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.service.SubscrActionGroupService;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.Longs;
import com.jayway.jsonpath.JsonPath;

public class SubscrActionControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrActionControllerTest.class);
	
	@Autowired
	private SubscrActionUserService subscrActionUserService;

	@Autowired
	private SubscrActionGroupService subscrActionGroupService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetGroup() throws Exception {
		testJsonGet("/api/subscr/subscrAction/groups");
	}

	/**
	 * 
	 * @throws Exception
	 */
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
	
		List<SubscrActionGroup> groupList = subscrActionGroupService.findAll(currentSubscriberService.getSubscriberId());
		List<Long> groupIds = new ArrayList<Long>();
		for (SubscrActionGroup s : groupList) {
			groupIds.add(s.getId());
		}
		 
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
				.param("subscrGroupIds", arrayToString(Longs.toArray(groupIds)))
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
		
		
		List<SubscrActionUser> userList = subscrActionUserService.findAll(currentSubscriberService.getSubscriberId());
		List<Long> userIds = new ArrayList<Long>();
		for (SubscrActionUser s : userList) {
			userIds.add(s.getId());
		}
		 		
		
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
				.param("subscrUserIds", arrayToString(Longs.toArray(userIds)))
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
