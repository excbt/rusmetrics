package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
@Ignore
public class UDirectoryNodeTest extends PortalApiTest {


	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryNodeTest.class);

	@Autowired
	private UDirectoryNodeService directoryNodeService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private UDirectoryNodeController uDirectoryNodeController;
	@Autowired
    private UDirectoryNodeService uDirectoryNodeService;
    @Autowired
    private UDirectoryService uDirectoryService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        uDirectoryNodeController = new UDirectoryNodeController(uDirectoryNodeService, uDirectoryService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(uDirectoryNodeController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
	public void testGetNodeDir() throws Exception {
        mockMvcRestWrapper.restRequest("/api/u_directory/{id}/node", UDirectoryTestConst.TEST_DIRECTORY_ID).testGet();
	}

	@Test
    @Ignore
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

		ResultActions resultActionsAll = restPortalMockMvc.perform(put(urlStr)
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

		ResultActions resultAction = restPortalMockMvc.perform(post(urlStr)
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
