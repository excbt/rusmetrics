package ru.excbt.datafuse.nmk.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringMvcConfig.class, JpaTestConfiguration.class})
public class WebTest {

	public final static String CHARSET_UTF8 = "charset=UTF-8";
	public final static String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON + ";" + CHARSET_UTF8;
	
	@Autowired
	private WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void test() {
		assertNotNull(mockMvc);
		
		try {
			restGetJson("/rest/check");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

	/**
	 * 
	 * @param urlTemplate
	 * @throws Exception
	 */
	protected void restGetJson(String urlTemplate) throws Exception {
		ResultActions resultActionsAll = mockMvc.perform(get(urlTemplate)
				.accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk()).andExpect(
				content().contentType(APPLICATION_JSON_UTF8));
	}

}
