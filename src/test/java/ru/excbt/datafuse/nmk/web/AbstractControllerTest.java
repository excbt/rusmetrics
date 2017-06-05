package ru.excbt.datafuse.nmk.web;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;
import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.MockUserService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractApiResource;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.ServletContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AbstractControllerTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractControllerTest.class);

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

    /*

     */
	protected void setupAuditor(long userId, long subscriberId) {
		this.auditorAware.setAuditUser(entityManager.getReference(V_AuditUser.class, userId));

		this.mockUserService.setMockUserId(userId);
		this.mockSubscriberService.setMockSubscriberId(subscriberId);
	}

    /*

     */
	protected String _testGetJson(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk())
					.andExpect(content().contentType(AbstractApiResource.APPLICATION_JSON_UTF8));
		};

		ResultActions resultActions = _testGetResultActions(url, requestExtraInitializer, resultActionsTester);

		return resultActions.andReturn().getResponse().getContentAsString();

	}

    /*

     */
	protected ResultActions _testGetJsonResultActions(String url) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk())
			.andExpect(content().contentType(AbstractApiResource.APPLICATION_JSON_UTF8));
		};

		return _testGetResultActions(url, requestExtraInitializer, resultActionsTester);
	}

    /*

     */
	protected String _testGetJson(String url, RequestExtraInitializer params) throws Exception {

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.accept(MediaType.APPLICATION_JSON);
			params.doInit(builder);
		};

		ResultActionsTester resultActionsTester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());

			resultActions.andExpect(status().isOk())
					.andExpect(content().contentType(AbstractApiResource.APPLICATION_JSON_UTF8));
		};

		ResultActions resultActions = _testGet(url, requestExtraInitializer, resultActionsTester);

		return resultActions.andReturn().getResponse().getContentAsString();
	}

    /*

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

    /*

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

    /*

     */
	protected ResultActions _testGet(String url, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		return _testGetResultActions(url, requestExtraInitializer, resultActionsTester);

	}

    /*

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

    /*

     */
	protected void _testGet(String url, RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().is2xxSuccessful());
		};

		_testGet(url, requestExtraInitializer, tester);
	}

    /*

     */
	protected void _testDeleteJson(String urlStr) throws Exception {
		_testDeleteJson(urlStr, null, null);
	}

    /*

     */
	protected void _testDeleteJson(String urlStr, Object sendObject) throws Exception {
		_testDeleteJson(urlStr, sendObject, null);

	}

    /*

     */
	protected void _testDeleteJson(String urlStr, RequestExtraInitializer requestExtraInitializer) throws Exception {

		_testDeleteJson(urlStr, null, requestExtraInitializer);

	}

    /*

     */
	protected void _testDeleteJson(String urlStr, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		log.info("Testing DELETE on URL: {}", urlStr);

		MockHttpServletRequestBuilder request = delete(urlStr).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = TestUtils.objectToJson(sendObject);
			request.contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		}

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions deleteResultActions = mockMvc.perform(request);

		deleteResultActions.andDo(MockMvcResultHandlers.print());
		deleteResultActions.andExpect(status().is2xxSuccessful());
	}

    /*

     */
	protected Long _testCreateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isCreated());
		};

		return _testCreateJson(url, sendObject, requestExtraInitializer, tester);

	}

    /*

     */
	protected Long _testCreateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		log.info("Testing CREATE on URL: {}", url);

		String jsonBody = TestUtils.objectToJson(sendObject);

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

    /*

     */
	protected Long _testCreateJson(String url, Object sendObject) throws Exception {

		return _testCreateJson(url, sendObject, null);
	}

    /*

     */
	protected void _testUpdateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		_testUpdateJson(url, sendObject, requestExtraInitializer, tester);
	}

    /*

     */
	protected void _testUpdateJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		log.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = TestUtils.objectToJson(sendObject);

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

	/*

	 */
	protected void _testUpdateJson(String url, Object sendObject) throws Exception {
		_testUpdateJson(url, sendObject, null);
	}


	/*

	 */
	protected ResultActions _testPutJson(String url, RequestExtraInitializer requestExtraInitializer) throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		return _testPutJson(url, null, requestExtraInitializer, tester);
	}

	/*

	 */
	protected ResultActions _testPutJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};

		return _testPutJson(url, sendObject, requestExtraInitializer, tester);
	}

	/*

	 */
	protected ResultActions _testPutJson(String url, Object sendObject)
			throws Exception {
		return _testPutJson(url, sendObject, null);
	}

	/*

	 */
	protected ResultActions _testPutJson(String url, Object sendObject, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		log.info("Testing UPDATE on URL: {}", url);

		MockHttpServletRequestBuilder request = put(url).with(testSecurityContext()).accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = TestUtils.objectToJson(sendObject);
			log.info("Request JSON: {}", jsonBody);
			request.contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		}

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

		return resultActions;
	}

    /*

     */
	protected ResultActions _testPutJson(String url) throws Exception {
		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().isOk());
		};
		return _testPutJson(url, null, null, tester);
		//_testUpdateJson(url, null, null);
	}


	/*

	 */
	protected ResultActions _testPostJson(String urlStr, Object sendObject,
			RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		log.info("Testing UPDATE on URL: {}", urlStr);

		MockHttpServletRequestBuilder request = post(urlStr).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON);

		if (sendObject != null) {
			String jsonBody = TestUtils.objectToJson(sendObject);
			request.contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		}

		if (requestExtraInitializer != null) {
			requestExtraInitializer.doInit(request);
		}

		ResultActions resultActions = mockMvc.perform(request);

		if (resultActionsTester != null) {
			resultActionsTester.testResultActions(resultActions);
		}

		return resultActions;

	}

    /*

     */
	protected ResultActions _testPostJson(String urlStr, RequestExtraInitializer requestExtraInitializer,
			ResultActionsTester resultActionsTester) throws Exception {

		return _testPostJson(urlStr, null, requestExtraInitializer, resultActionsTester);

	}

    /*

     */
	protected ResultActions _testPostJson(String urlStr, RequestExtraInitializer requestExtraInitializer)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		return _testPostJson(urlStr, null, requestExtraInitializer, tester);

	}

    /*

     */
	protected ResultActions _testPostJson(String urlStr, Object sendObject)
			throws Exception {

		ResultActionsTester tester = (resultActions) -> {
			resultActions.andDo(MockMvcResultHandlers.print());
			resultActions.andExpect(status().is2xxSuccessful());
		};

		return _testPostJson(urlStr, sendObject, null, tester);

	}




}
