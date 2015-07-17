package ru.excbt.datafuse.nmk.web;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

public class WebTest extends AnyControllerTest {

	public final static String CHARSET_UTF8 = "charset=UTF-8";
	public final static String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON
			+ ";" + CHARSET_UTF8;

	private static final Logger logger = LoggerFactory.getLogger(WebTest.class);

	@Autowired
	private WebAppPropsService webAppPropsService;

	@Test
	public void test() {

		logger.info("WebAppHomeDirectory:{}",
				webAppPropsService.getWebAppHomeDirectory());

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
