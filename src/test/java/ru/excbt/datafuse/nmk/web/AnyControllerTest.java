package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ru.excbt.datafuse.nmk.config.jpa.JpaTestConfiguration;
import ru.excbt.datafuse.nmk.config.mvc.SpringMvcConfig;
import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.MockUserService;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringMvcConfig.class, JpaTestConfiguration.class})
@WithMockUser(username = "admin", password = "admin", roles = { "ADMIN",
		"SUBSCR_ADMIN", "SUBSCR_USER" })
public class AnyControllerTest {

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

	@Before
	public void setup() {
		this.auditorAware.setAuditUser(entityManager.getReference(AuditUser.class,
				TEST_AUDIT_USER));
		
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
	 * @param urlTemplate
	 * @throws Exception
	 */
	protected void testJsonGet(String urlTemplate) throws Exception {
		ResultActions resultActionsAll = mockMvc.perform(get(urlTemplate).with(
				testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiController.APPLICATION_JSON_UTF8));
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
	 * @param urlStr
	 * @throws Exception
	 */
	protected void testJsonDelete(String urlStr) throws Exception {

		ResultActions deleteResultActions = mockMvc
				.perform(delete(urlStr).with(testSecurityContext()).accept(
						MediaType.APPLICATION_JSON));

		deleteResultActions.andDo(MockMvcResultHandlers.print());
		deleteResultActions.andExpect(status().isNoContent());
	}
	
}
