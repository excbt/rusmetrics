package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfigCli;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.config.mvc.SpringMvcConfig;
import ru.excbt.datafuse.nmk.config.security.LocalSecurityConfig;
import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.MockUserService;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringMvcConfig.class, JpaConfigCli.class,
		LocalSecurityConfig.class, LdapConfig.class })
@WithMockUser(username = "admin", password = "admin", roles = { "ADMIN",
		"SUBSCR_ADMIN", "SUBSCR_USER" })
public class AnyControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(AnyControllerTest.class);

	private final static long TEST_AUDIT_USER = 1;
	public static final long DEV_SUBSCR_ORG_ID = 728;

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	protected MockAuditorAware auditorAware;

	@Autowired
	protected MockUserService mockUserService;

	@Autowired
	protected MockSubscriberService mockSubscriberService;

	protected MockMvc mockMvc;

	@Autowired
	protected ServletContext servletContext;

	@Before
	public void setup() {
		this.auditorAware.setAuditUser(entityManager.getReference(
				AuditUser.class, TEST_AUDIT_USER));

		this.mockUserService.setMockUserId(TEST_AUDIT_USER);
		this.mockSubscriberService.setMockSubscriberId(DEV_SUBSCR_ORG_ID);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void testInit() {
		checkNotNull(mockMvc);
	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void testJsonGet(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk()).andExpect(
					content().contentType(
							WebApiController.APPLICATION_JSON_UTF8));
		};

		testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void testJsonGetNoJsonCheck(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void testJsonGetSuccessfull(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().is2xxSuccessful());
		};

		testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void testHtmlGet(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.TEXT_HTML);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andExpect(status().isOk()).andExpect(
					content().contentType(MediaType.TEXT_HTML));
		};

		testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void testGet(String url,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		MockHttpServletRequestBuilder request = get(url).with(
				testSecurityContext());

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

	}

	/**
	 * 
	 * @param url
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void testGet(String url,
			RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print()).andExpect(
					status().is2xxSuccessful());
		};

		testGet(url, requestExtraInitializer, tester);
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	protected static String arrayToString(long[] a) {
		if (a == null)
			return "null";
		int iMax = a.length - 1;
		if (iMax == -1)
			return "";

		StringBuilder b = new StringBuilder();
		for (int i = 0;; i++) {
			b.append(a[i]);
			if (i == iMax)
				return b.toString();
			b.append(", ");
		}
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	protected static String listToString(List<Long> a) {
		if (a == null)
			return "null";
		int iMax = a.size() - 1;
		if (iMax == -1)
			return "";

		StringBuilder b = new StringBuilder();
		for (int i = 0;; i++) {
			b.append(a.get(i));
			if (i == iMax)
				return b.toString();
			b.append(", ");
		}
	}

	/**
	 * 
	 * @param urlStr
	 * @throws Exception
	 */
	protected void testJsonDelete(String urlStr) throws Exception {

		logger.info("Testing DELETE on URL: {}", urlStr);

		ResultActions deleteResultActions = mockMvc
				.perform(delete(urlStr).with(testSecurityContext()).accept(
						MediaType.APPLICATION_JSON));

		deleteResultActions.andDo(MockMvcResultHandlers.print());
		deleteResultActions.andExpect(status().is2xxSuccessful());
	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @param requestExtraInitializer
	 * @return
	 * @throws Exception
	 */
	protected Long testJsonCreate(String url, Object sendObject,
			RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isCreated());
		};

		return testJsonCreate(url, sendObject, requestExtraInitializer, tester);

	}

	/**
	 * 
	 * @param url
	 * @param sendObject
	 * @param requestExtraInitializer
	 * @param resultActionsTester
	 * @return
	 * @throws Exception
	 */
	protected Long testJsonCreate(String url, Object sendObject,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing CREATE on URL: {}", url);

		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(sendObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		logger.info("Request JSON: {}", jsonBody);

		MockHttpServletRequestBuilder request = post(url)
				.contentType(MediaType.APPLICATION_JSON).content(jsonBody)
				.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

		String jsonContent = resultActions.andReturn().getResponse()
				.getContentAsString();
		Integer createdId = JsonPath.read(jsonContent, "$.id");
		assertTrue(createdId > 0);
		return Long.valueOf(createdId);

	}

	/**
	 * 
	 * @param sendObject
	 * @return
	 * @throws Exception
	 */
	protected Long testJsonCreate(String url, Object sendObject)
			throws Exception {

		return testJsonCreate(url, sendObject, null);
	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void testJsonUpdate(String url, Object sendObject,
			RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		testJsonUpdate(url, sendObject, requestExtraInitializer, tester);
	}

	/**
	 * 
	 * @param url
	 * @param sendObject
	 * @param requestExtraInitializer
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void testJsonUpdate(String url, Object sendObject,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(
				testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = null;
			try {
				jsonBody = OBJECT_MAPPER.writeValueAsString(sendObject);
			} catch (JsonProcessingException e) {
				logger.error("Can't create json: {}", e);
				e.printStackTrace();
				fail();
			}

			logger.info("Request JSON: {}", jsonBody);

			request.contentType(MediaType.APPLICATION_JSON).content(jsonBody);

		}

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @throws Exception
	 */
	protected void testJsonUpdate(String url, Object sendObject)
			throws Exception {
		testJsonUpdate(url, sendObject, null);
	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void testJsonPut(String url,
			RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		testJsonPut(url, null, requestExtraInitializer, tester);
	}

	/**
	 * 
	 * @param url
	 * @param sendObject
	 * @param requestExtraInitializer
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void testJsonPut(String url, Object sendObject,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(
				testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = null;
			try {
				jsonBody = OBJECT_MAPPER.writeValueAsString(sendObject);
			} catch (JsonProcessingException e) {
				logger.error("Can't create json: {}", e);
				e.printStackTrace();
				fail();
			}

			logger.info("Request JSON: {}", jsonBody);

			request.contentType(MediaType.APPLICATION_JSON).content(jsonBody);

		}

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @throws Exception
	 */
	protected void testJsonPut(String url) throws Exception {
		testJsonUpdate(url, null, null);
	}

	/**
	 * 
	 * @param urlStr
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void testJsonPost(String urlStr,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", urlStr);

		MockHttpServletRequestBuilder request = post(urlStr).with(
				testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

	}

	/**
	 * 
	 * @param urlStr
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void testJsonPost(String urlStr,
			RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		testJsonPost(urlStr, requestExtraInitializer, tester);

	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected String apiSubscrUrl(String url) {
		checkNotNull(url);
		return "/api/subscr" + url;
	}

}
