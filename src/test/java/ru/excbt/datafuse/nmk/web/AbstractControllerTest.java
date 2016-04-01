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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.MockUserService;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

public class AbstractControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(AbstractControllerTest.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager entityManager;

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected Filter springSecurityFilterChain;

	@Autowired
	protected MockAuditorAware auditorAware;

	@Autowired
	protected MockUserService mockUserService;

	@Autowired
	protected MockSubscriberService mockSubscriberService;

	protected MockMvc mockMvc;

	@Autowired
	protected ServletContext servletContext;

	/**
	 * 
	 * @param userId
	 * @param subscriberId
	 */
	protected void setupAuditor(long userId, long subscriberId) {
		this.auditorAware.setAuditUser(entityManager.getReference(AuditUser.class, userId));

		this.mockUserService.setMockUserId(userId);
		this.mockSubscriberService.setMockSubscriberId(subscriberId);
	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected String _testGetJson(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk())
					.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));
		};

		ResultActions resultActions = _testGetResultActions(url, requestExtraInitializer, resultActionsTester);

		return resultActions.andReturn().getResponse().getContentAsString();

	}

	/**
	 * 
	 * @param url
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void _testGetJson(String url, RequestExtraInitializer params) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
			params.doInit(builder);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk())
					.andExpect(content().contentType(WebApiController.APPLICATION_JSON_UTF8));
		};

		_testGet(url, requestExtraInitializer, resultActionsTester);
	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void _testGetSuccessful(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		_testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @throws Exception
	 */
	protected void _testGetHtml(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.TEXT_HTML);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.TEXT_HTML));
		};

		_testGet(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void _testGet(String url, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		_testGetResultActions(url, requestExtraInitializer, resultActionsTester);

	}

	/**
	 * 
	 * @param url
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected ResultActions _testGetResultActions(String url, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		MockHttpServletRequestBuilder request = get(url).with(testSecurityContext());

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

		return resultActions;
	}

	/**
	 * 
	 * @param url
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void _testGet(String url, RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().is2xxSuccessful());
		};

		_testGet(url, requestExtraInitializer, tester);
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
	protected static String listToString(List<?> a) {
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
	 * @param type
	 * @param jsonPacket
	 * @return
	 */
	public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
		T data = null;

		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			logger.error("Can't read JSON:");
			logger.error(jsonPacket);
			logger.error("exception: ", e);
		}
		return data;
	}

	/**
	 * 
	 * @param urlStr
	 * @throws Exception
	 */
	protected void _testDeleteJson(String urlStr) throws Exception {

		logger.info("Testing DELETE on URL: {}", urlStr);

		ResultActions deleteResultActions = mockMvc
				.perform(delete(urlStr).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		deleteResultActions.andDo(MockMvcResultHandlers.print());
		deleteResultActions.andExpect(status().is2xxSuccessful());
	}

	/**
	 * 
	 * @param urlStr
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void _testDeleteJson(String urlStr, RequestExtraInitializer requestExtraInitializer) throws Exception {

		logger.info("Testing DELETE on URL: {}", urlStr);

		MockHttpServletRequestBuilder request = delete(urlStr).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON);

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions deleteResultActions = mockMvc.perform(request);

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
	protected Long _testCreateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isCreated());
		};

		return _testCreateJson(url, sendObject, requestExtraInitializer, tester);

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
	protected Long _testCreateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing CREATE on URL: {}", url);

		String jsonBody = null;
		String jsonBodyPretty = null;
		try {
			if (!(sendObject instanceof String)) {
				jsonBody = OBJECT_MAPPER.writeValueAsString(sendObject);
				jsonBodyPretty = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(sendObject);
			} else {
				jsonBody = (String) sendObject;
				jsonBodyPretty = (String) sendObject;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		logger.info("Request JSON: {}", jsonBody);
		logger.info("Request Pretty JSON: {}", jsonBodyPretty);

		MockHttpServletRequestBuilder request = post(url).contentType(MediaType.APPLICATION_JSON).content(jsonBody)
				.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

		String jsonContent = resultActions.andReturn().getResponse().getContentAsString();
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
	protected Long _testCreateJson(String url, Object sendObject) throws Exception {

		return _testCreateJson(url, sendObject, null);
	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void _testUpdateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		_testUpdateJson(url, sendObject, requestExtraInitializer, tester);
	}

	/**
	 * 
	 * @param url
	 * @param sendObject
	 * @param requestExtraInitializer
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void _testUpdateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = null;
			String jsonBodyPretty = null;
			try {
				if (!(sendObject instanceof String)) {
					jsonBody = OBJECT_MAPPER.writeValueAsString(sendObject);
					jsonBodyPretty = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(sendObject);
				} else {
					jsonBody = (String) sendObject;
					jsonBodyPretty = (String) sendObject;
				}
			} catch (JsonProcessingException e) {
				logger.error("Can't create json: {}", e);
				e.printStackTrace();
				fail();
			}

			logger.info("Request JSON: {}", jsonBody);
			logger.info("Request Pretty JSON: {}", jsonBodyPretty);

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
	protected void _testUpdateJson(String url, Object sendObject) throws Exception {
		_testUpdateJson(url, sendObject, null);
	}

	/**
	 * 
	 * @param sendObject
	 * @param url
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void _testPutJson(String url, RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		_testPutJson(url, null, requestExtraInitializer, tester);
	}

	/**
	 * 
	 * @param url
	 * @param sendObject
	 * @param requestExtraInitializer
	 * @param resultActionsTester
	 * @throws Exception
	 */
	protected void _testPutJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

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
	protected void _testPutJson(String url) throws Exception {
		_testUpdateJson(url, null, null);
	}

	/**
	 * 
	 * @param urlStr
	 * @param requestExtraInitializer
	 * @throws Exception
	 */
	protected void _testPostJson(String urlStr, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		logger.info("Testing UPDATE on URL: {}", urlStr);

		MockHttpServletRequestBuilder request = post(urlStr).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON);

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
	protected void _testPostJson(String urlStr, RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		_testPostJson(urlStr, requestExtraInitializer, tester);

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

	/**
	 * 
	 * @param url
	 * @param args
	 * @return
	 */
	protected String apiSubscrUrl(String url, Object... args) {
		checkNotNull(url);
		return "/api/subscr" + String.format(url, args);
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected String apiSubscrUrl(String url, Long id) {
		checkNotNull(url);
		checkNotNull(id);
		return stringBuilderUtil("/api/subscr", url, "/", id.toString());
	}

	/**
	 * 
	 * @param url
	 * @param id
	 * @return
	 */
	protected String apiSubscrUrl(String url, Integer id) {
		checkNotNull(url);
		checkNotNull(id);
		return stringBuilderUtil("/api/subscr", url, "/", id.toString());
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected String apiRmaUrl(String url) {
		checkNotNull(url);
		return "/api/rma" + url;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected String apiRmaUrl(String url, Object... args) {
		checkNotNull(url);
		return "/api/rma" + String.format(url, args);
	}

	/**
	 * 
	 * @param url
	 * @param id
	 * @return
	 */
	protected String apiRmaUrl(String url, Long id) {
		checkNotNull(url);
		checkNotNull(id);
		return stringBuilderUtil("/api/rma", url, "/", id.toString());
	}

	/**
	 * 
	 * @param url
	 * @param id
	 * @return
	 */
	protected String apiRmaUrl(String url, Integer id) {
		checkNotNull(url);
		checkNotNull(id);
		return stringBuilderUtil("/api/rma", url, "/", id.toString());
	}

	/**
	 * 
	 * @param strings
	 * @return
	 */
	private String stringBuilderUtil(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}

}
