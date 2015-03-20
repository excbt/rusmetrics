package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import ru.excbt.datafuse.nmk.web.api.WebApiConst;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:META-INF/spring/app-config.xml",
		                           "classpath:META-INF/spring/servlet-context.xml"})
@WithMockUser(username = "admin", password = "admin", roles = { "ADMIN" })
public class AnyControllerTest {
	

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private Filter springSecurityFilterChain;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
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
		ResultActions resultActionsAll = mockMvc.perform(get(
				urlTemplate).with(testSecurityContext())
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(WebApiConst.APPLICATION_JSON_UTF8));		
	}
}
