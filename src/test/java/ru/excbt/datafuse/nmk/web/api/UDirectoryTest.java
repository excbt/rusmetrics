package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

@Transactional
public class UDirectoryTest extends AnyControllerTest {

	public final static String DIRECTORY_URL_API = "/api/u_directory";
	public final static long TEST_DIRECTORY_ID = 19748782;

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryTest.class);

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testDirectoryGetAll() throws Exception {
		_testGetJson(String.format(DIRECTORY_URL_API));
	}

	@Test
	public void testParamsGetOne() throws Exception {
		String urlStr = String.format(DIRECTORY_URL_API + "/%d", TEST_DIRECTORY_ID);
		_testGetJson(urlStr);
	}

	@Test
	public void testDirectoryUpdate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		UDirectory d = directoryService.findOne(currentSubscriberService.getSubscriberId(), TEST_DIRECTORY_ID);

		d.setDirectoryDescription("TEST Param - Name " + System.currentTimeMillis());

		String jsonBody = mapper.writeValueAsString(d);

		logger.info("Updated Directory Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API + "/%d", TEST_DIRECTORY_ID);

		ResultActions resultActionsAll = mockMvc.perform(put(urlStr).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk());
	}

	@Test
	public void testDirectoryCreateDelete() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectory d = new UDirectory();

		d.setDirectoryName("TEST Param - Name " + System.currentTimeMillis());

		String jsonBody = mapper.writeValueAsString(d);

		logger.info("New Directory Param Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API);

		ResultActions resultAction = mockMvc.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().isCreated());

		String jsonContent = resultAction.andReturn().getResponse().getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);

		logger.info("Testing delete Param id: {}", createdId);

		String urlStrDelete = String.format(DIRECTORY_URL_API + "/%d", createdId);

		_testDeleteJson(urlStrDelete);

	}

}
