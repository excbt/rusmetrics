package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

public class ReferencePeriodControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReferencePeriodControllerTest.class);

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private SubscriberService subscriberService;

	private Long getOId() {
		List<Long> vList = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());
		assertTrue(vList.size() > 0);
		return vList.get(0);
	}

	private Long getZPointId(Long oId) {
		List<Long> vList2 = contZPointService.selectContZPointIds(oId);
		assertTrue(vList2.size() > 0);
		return vList2.get(0);
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Test
	public void testGetAll() throws Exception {

		Long oId = getOId();
		Long zpId = getZPointId(oId);
		testJsonGet(String.format(
				"/api/subscr/contObjects/%d/zpoints/%d/referencePeriod", oId,
				zpId));
	}

	@Test
	public void testCreateDelete() throws Exception {
		Long oId = getOId();
		Long zpId = getZPointId(oId);
		String urlStr = String.format(
				"/api/subscr/contObjects/%d/zpoints/%d/referencePeriod", oId,
				zpId);

		// Create testing
		ReferencePeriod referencePeriod = new ReferencePeriod();
		referencePeriod.setBeginDate(new Date());
		referencePeriod.setPeriodDescription("Testing ReferencePeriod");
		referencePeriod.setTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname());

		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(referencePeriod);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		ResultActions resultAction = mockMvc
				.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody).with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);
		assertTrue(createdId > 0);

		// Update testing
		referencePeriod.setId(Long.valueOf(createdId));

		referencePeriod.setPeriodDescription("Testing Update");

		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(referencePeriod);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		resultAction = mockMvc
				.perform(put(urlStr + "/" + createdId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody).with(testSecurityContext())
						.accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isOk());

		// Delete testing
		testJsonDelete(urlStr + "/" + createdId);
	}
}
