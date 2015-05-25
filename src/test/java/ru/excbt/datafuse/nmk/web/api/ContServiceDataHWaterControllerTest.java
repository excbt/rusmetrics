package ru.excbt.datafuse.nmk.web.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ContServiceDataHWaterControllerTest extends AnyControllerTest {

	
	public final static String API_SERVICE_URL = "/api/subscr";	
	public final static String API_SERVICE_URL_TEMPLATE = API_SERVICE_URL + "/%d/service/24h/%d";	
	public final static long CONT_OBJECT_ID = 18811504;
	public final static long CONT_ZPOINT_ID = 18811557;
	public final static long CONT_OBJECT2_ID = 18811519;
	public final static long CONT_ZPOINT2_ID = 18811586;
	
	
	@Test
	public void testHWater24h() throws Exception {
		
		String urlStr = String.format(API_SERVICE_URL_TEMPLATE, CONT_OBJECT_ID, CONT_ZPOINT_ID);

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-10-01")
				.param("endDate", "2013-10-31")
				.with(testSecurityContext()));
		
		resultAction.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testHWater24hPaged() throws Exception {
		
		String urlStr = String.format(API_SERVICE_URL_TEMPLATE + "/paged?page=0&size=100", CONT_OBJECT_ID, CONT_ZPOINT_ID);
		
		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-10-01")
				.param("endDate", "2013-10-31")
				.with(testSecurityContext()));
		
		resultAction.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testHWaterSummary() throws Exception {
		
		String urlStr = String.format(API_SERVICE_URL_TEMPLATE + "/summary", CONT_OBJECT2_ID, CONT_ZPOINT2_ID);
		
		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2015-05-19")
				.param("endDate", "2015-05-25")
				.with(testSecurityContext()));
		
		resultAction.andDo(MockMvcResultHandlers.print());
	}
	
}
