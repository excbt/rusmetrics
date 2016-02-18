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

import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class UDirectoryNodeTest extends AnyControllerTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryNodeTest.class);
	
	@Autowired
	private UDirectoryNodeService directoryNodeService;

	@Test
	public void testGetNodeDir() throws Exception {
		_testGetJson(String.format(UDirectoryTestConst.DIRECTORY_URL_API
				+ "/%d/node", UDirectoryTestConst.TEST_DIRECTORY_ID));
	}

	@Test
	public void testUpdate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectoryNode directoryNode = directoryNodeService
				.getRootNode(UDirectoryTestConst.TEST_DIRECTORY_NODE_ID);
		
		directoryNode.setNodeComment("Node comment " + System.currentTimeMillis());
		
		for (UDirectoryNode ch : directoryNode.getChildNodes()) {
			ch.setNodeComment("Node comment " + System.currentTimeMillis());
		}
		
		
		String jsonContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(directoryNode);
		
		logger.info("Updated directoryNode JSON: {}", jsonContent);
		
		String urlStr = String.format(UDirectoryTestConst.DIRECTORY_URL_API
				+ "/%d/node/%d", UDirectoryTestConst.TEST_DIRECTORY_ID, UDirectoryTestConst.TEST_DIRECTORY_NODE_ID);

		ResultActions resultActionsAll = mockMvc.perform(put(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)
				.with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk());		
		
	}
	
	@Test
	public void testCreate() throws Exception {

		String urlStr = String.format(UDirectoryTestConst.DIRECTORY_URL_API
				+ "/%d/node", 19875915);
		
		String jsonNew = "{\"nodeName\": \"1212\",\"childNodes\": [{\"nodeName\": \"1212.1\",\"childNodes\": [{\"nodeName\": \"1212.1.1\",\"childNodes\": []}]}]}";

		ResultActions resultAction = mockMvc.perform(post(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonNew)
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
		
		
	}
	
}
