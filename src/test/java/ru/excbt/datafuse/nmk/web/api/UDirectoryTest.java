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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
@Ignore
public class UDirectoryTest extends PortalApiTest {

	public final static String DIRECTORY_URL_API = "/api/u_directory";
	public final static long TEST_DIRECTORY_ID = 19748782;

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryTest.class);

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private UDirectoryController uDirectoryController;

    @Autowired
    private UDirectoryService uDirectoryService;
    @Autowired
    private UDirectoryNodeService directoryNodeService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        uDirectoryController = new UDirectoryController(uDirectoryService, directoryNodeService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(uDirectoryController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	@Test
	public void testDirectoryGetAll() throws Exception {
        mockMvcRestWrapper.restRequest("/api/u_directory").testGet();
//		_testGetJson(String.format(DIRECTORY_URL_API));
	}

	@Test
	public void testParamsGetOne() throws Exception {
        mockMvcRestWrapper.restRequest("/api/u_directory/{id}", TEST_DIRECTORY_ID).testGet();
//		String urlStr = String.format(DIRECTORY_URL_API + "/%d", TEST_DIRECTORY_ID);
//		_testGetJson(urlStr);
	}

	@Test
    @Ignore
	public void testDirectoryUpdate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		UDirectory d = directoryService.findOne(currentSubscriberService.getSubscriberId(), TEST_DIRECTORY_ID);

		d.setDirectoryDescription("TEST Param - Name " + System.currentTimeMillis());

		String jsonBody = mapper.writeValueAsString(d);

		logger.info("Updated Directory Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API + "/%d", TEST_DIRECTORY_ID);

		ResultActions resultActionsAll = restPortalMockMvc.perform(put(urlStr).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk());
	}

	@Test
	public void testDirectoryCreateDelete() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectory uDirectory = new UDirectory();

		uDirectory.setDirectoryName("TEST Param - Name " + System.currentTimeMillis());

		String jsonBody = mapper.writeValueAsString(uDirectory);

		logger.info("New Directory Param Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API);

        ResultActions resultActions = mockMvcRestWrapper.restRequest("/api/u_directory")
            .testPost(uDirectory)
            .testGetAndReturn()
//            .andDo(MockMvcResultHandlers.print());
            .andExpect(status().is2xxSuccessful());

//        Long createdId = mockMvcRestWrapper.getLastResponseId();
        String jsonContent = resultActions.andReturn().getResponse().getContentAsString();
        Integer createdId = JsonPath.read(jsonContent, "$.id");
//		ResultActions resultAction = restPortalMockMvc.perform(post(urlStr).contentType(MediaType.APPLICATION_JSON)
//				.content(jsonBody).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));
//
//		resultAction.andDo(MockMvcResultHandlers.print());
//
//		resultAction.andExpect(status().isCreated());
//
//		String jsonContent = resultAction.andReturn().getResponse().getContentAsString();
//		Integer createdId = JsonPath.read(jsonContent, "$.id");
		logger.info("createdId: {}", createdId);

		logger.info("Testing delete Param id: {}", createdId);

//		String urlStrDelete = String.format(DIRECTORY_URL_API + "/%d", createdId);

        mockMvcRestWrapper.restRequest("/api/u_directory/{id}", createdId).testDelete();

//		_testDeleteJson(urlStrDelete);

	}

}
