package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class UDirectoryParamTest extends AnyControllerTest {

	public final static String DIRECTORY_URL_API = "/api/u_directory";
	public final static long TEST_DIRECTORY_ID = 19748782;
	public final static long TEST_DIRECTORY_PARAM_ID = 19748790;

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryParamTest.class);

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;


	@Test
	public void testParamsGetAll() throws Exception {
		_testJsonGet(String.format(DIRECTORY_URL_API + "/%d/param",
				TEST_DIRECTORY_ID));
	}

	@Test
	public void testParamsGetOne() throws Exception {
		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param/%d",
				TEST_DIRECTORY_ID, TEST_DIRECTORY_PARAM_ID);		
		_testJsonGet(urlStr);
	}

	@Test
	public void testParamsUpdate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectoryParam p = directoryParamService
				.findOne(TEST_DIRECTORY_PARAM_ID);

		p.setParamName("TEST Param - Name " + System.currentTimeMillis());
		
		String jsonBody = mapper.writeValueAsString(p);
		
		logger.info("Updated Directory Param Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param/%d",
				TEST_DIRECTORY_ID, TEST_DIRECTORY_PARAM_ID);

		ResultActions resultActionsAll = mockMvc.perform(put(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk());
	}

	
	@Test
	public void testParamsCreateDelete() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectoryParam p = new UDirectoryParam();
		
		p.setParamName("TEST Param - Name " + System.currentTimeMillis());
		p.setParamType(ParamType.BOOLEAN.name());
		
		String jsonBody = mapper.writeValueAsString(p);
		
		logger.info("New Directory Param Source JSON: {}", jsonBody);
		
		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param",
				TEST_DIRECTORY_ID);
		
		ResultActions resultAction = mockMvc.perform(post(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				.with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));
		
		resultAction.andDo(MockMvcResultHandlers.print());
		
		resultAction.andExpect(status().isCreated());
		
		String jsonContent = resultAction.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent,
				"$.id");
		logger.info("createdId: {}", createdId);		

		logger.info("Testing delete Param id: {}", createdId);		
		
		
		String urlStrDelete = String.format(DIRECTORY_URL_API + "/%d/param/%d",
				TEST_DIRECTORY_ID, createdId);
		
		_testJsonDelete(urlStrDelete);
	}
	
	

}
