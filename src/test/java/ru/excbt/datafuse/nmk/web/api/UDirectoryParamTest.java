package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Optional;

@RunWith(SpringRunner.class)
@Ignore
public class UDirectoryParamTest extends PortalApiTest {

	public final static String DIRECTORY_URL_API = "/api/u_directory";
	public final static long TEST_DIRECTORY_ID = 19748782;
	public final static long TEST_DIRECTORY_PARAM_ID = 19748790;

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryParamTest.class);

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private UDirectoryParamController uDirectoryParamController;
	@Autowired
    private UDirectoryParamService uDirectoryParamService;
    @Autowired
	private UDirectoryService uDirectoryService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        uDirectoryParamController = new UDirectoryParamController(uDirectoryParamService, uDirectoryService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(uDirectoryParamController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}



	@Test
	public void testParamsGetAll() throws Exception {
        mockMvcRestWrapper.restRequest("/api/u_directory/{id}/param", TEST_DIRECTORY_ID).testGet();
//		_testGetJson(String.format(DIRECTORY_URL_API + "/%d/param",
//				TEST_DIRECTORY_ID));
	}

	@Test
	public void testParamsGetOne() throws Exception {
//		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param/%d",
//				TEST_DIRECTORY_ID, TEST_DIRECTORY_PARAM_ID);
        mockMvcRestWrapper.restRequest("/api/u_directory/{id1}/param/id2", TEST_DIRECTORY_ID, TEST_DIRECTORY_PARAM_ID).testGet();
//		_testGetJson(urlStr);
	}

	@Test
    @Ignore
	public void testParamsUpdate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UDirectoryParam p = directoryParamService
				.findOne(TEST_DIRECTORY_PARAM_ID);

		p.setParamName("TEST Param - Name " + System.currentTimeMillis());

		String jsonBody = mapper.writeValueAsString(p);

		logger.info("Updated Directory Param Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param/%d",
				TEST_DIRECTORY_ID, TEST_DIRECTORY_PARAM_ID);

		ResultActions resultActionsAll = restPortalMockMvc.perform(put(urlStr)
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
		UDirectoryParam param = new UDirectoryParam();

		param.setParamName("TEST Param - Name " + System.currentTimeMillis());
		param.setParamType(ParamType.BOOLEAN.name());

		String jsonBody = mapper.writeValueAsString(param);

		logger.info("New Directory Param Source JSON: {}", jsonBody);

		String urlStr = String.format(DIRECTORY_URL_API + "/%d/param",
				TEST_DIRECTORY_ID);

        ResultActions resultAction = mockMvcRestWrapper.restRequest(urlStr).testPostAndReturn(param, Optional.empty());

//		ResultActions resultAction = restPortalMockMvc.perform(post(urlStr)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(jsonBody)
//				.with(testSecurityContext())
//				.accept(MediaType.APPLICATION_JSON));

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

        mockMvcRestWrapper.restRequest(urlStrDelete).testDelete();
//		_testDeleteJson(urlStrDelete);
	}



}
