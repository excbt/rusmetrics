package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ContGroup;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

public class ContGroupControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContGroupControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContGroupService contGroupService;

	@Test
	public void testContGroupObjects() throws Exception {
		_testJsonGet("/api/contGroup/0/contObject/available");
	}

	@Test
	public void testContGroup() throws Exception {
		_testJsonGet("/api/contGroup");
	}

	@Test
	public void testCreateContGroup() throws Exception {

		ContGroup group = new ContGroup();
		group.setContGroupName("Новая группа");
		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(group);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		long[] objectIds = { 18811504L, 18811505L };

		logger.info("Array of {}", arrayToString(objectIds));
		logger.info("jsonBody: {}", jsonBody);

		ResultActions resultAction = mockMvc
				.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
						.post("/api/contGroup")
						.contentType(MediaType.APPLICATION_JSON)
						.param("contObjectIds", arrayToString(objectIds))
						.content(jsonBody).with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);

		testDeleteContGroup(createdId.longValue());
	}

	@Test
	public void testUpdateContGroup() throws Exception {
		List<ContGroup> contGroups = contGroupService
				.selectSubscriberGroups(currentSubscriberService
						.getSubscriberId());

		assertTrue(contGroups.size() > 0);
		ContGroup cg;
		if (contGroups.size() > 2) {
			cg = contGroups.get(1);
		} else {
			cg = contGroups.get(0);
		}

		long[] objectIds = { 18811522L, 18811533L };

		logger.info("Array of {}", arrayToString(objectIds));

		cg.setContGroupComment("TEST AutoUpdate " + System.currentTimeMillis());
		String jsonBody = OBJECT_MAPPER.writeValueAsString(cg);
		String urlStr = "/api/contGroup/" + cg.getId();

		ResultActions resultActionsAll;
		try {
			resultActionsAll = mockMvc.perform(put(urlStr)
					.contentType(MediaType.APPLICATION_JSON).content(jsonBody)
					.param("contObjectIds", arrayToString(objectIds))
					.with(testSecurityContext())
					.accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/*
	 * @Test
	 * 
	 * @Ignore public void testDeleteContGroup() throws Exception {
	 * List<ContGroup> contGroups = contGroupService
	 * .selectSubscriberGroups(currentSubscriberService .getSubscriberId());
	 * 
	 * assertTrue(contGroups.size() > 0); ContGroup cg; if (contGroups.size() >
	 * 2) { cg = contGroups.get(1); } else { cg = contGroups.get(0); } String
	 * urlStr = "/api/contGroup/" + cg.getId(); testJsonDelete(urlStr); }
	 */

	public void testDeleteContGroup(Long contGroupId) throws Exception {
		_testJsonDelete("/api/contGroup/" + contGroupId);
	}

}
