package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputType;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportTest extends AnyControllerTest {


	public final static String API_REPORT_URL = "/api/report";	
	
	private static final Logger logger = LoggerFactory
			.getLogger(ReportTest.class);
	
	
	
	private void redirectOption(ReportOutputType reportType) throws Exception {
		assertNotNull(reportType);
		
		String urlStr = String.format(API_REPORT_URL + "/commercial/%d/%s", 18811505, reportType.toLowerName());

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-03-01")
				.param("endDate", "2013-03-31")
				.with(testSecurityContext()));
		
		resultAction.andDo(MockMvcResultHandlers.print());
		
		String redirectedUrl = resultAction.andReturn().getResponse().getRedirectedUrl();
		assertNotNull(redirectedUrl);
		
		logger.debug("testReportHtmlRedirect. Redirected: {}", redirectedUrl);
		
	}
	
	@Test
	public void testReportHtmlRedirect() throws Exception {
		redirectOption(ReportOutputType.HTML);
	}

	@Test
	public void testReportPdfRedirect() throws Exception {
		redirectOption(ReportOutputType.PDF);
	}
	
	

	

}
