package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;

import com.google.common.primitives.Longs;

public class ReportServiceControllerTest extends AnyControllerTest {

	public final static String API_REPORT_URL = "/api/reportService";

	private final static long TEST_PARAMSET_COMMERCE = 28618264;
	private final static long TEST_PARAMSET_CONS_T1 = 29457574;
	private final static long TEST_PARAMSET_CONS_T2 = 28841247;
	private final static long TEST_PARAMSET_EVENT = 28820616;

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceControllerTest.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportService reportService;

	private void redirectOption(ReportOutputFileType reportType)
			throws Exception {
		assertNotNull(reportType);

		String urlStr = String.format(API_REPORT_URL + "/commerce/%d/%s",
				18811505, reportType.toLowerName());

		ResultActions resultAction = mockMvc.perform(get(urlStr)
				.contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-03-01")
				.param("endDate", "2013-03-31").with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());

		String redirectedUrl = resultAction.andReturn().getResponse()
				.getRedirectedUrl();
		assertNotNull(redirectedUrl);

		logger.debug("testReportHtmlRedirect. Redirected: {}", redirectedUrl);

	}

	/**
	 * @throws Exception
	 * 
	 */
	@Test
	public void testCommerceDownloadGet() throws Exception {
		int reportParamsetId = 28618264;
		String urlStr = String.format(
				"/api/reportService/commerce/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportService
				.getReportMakerParam(reportParamsetId);

		ResultActions resultAction = mockMvc.perform(get(urlStr).contentType(
				MediaType.APPLICATION_JSON).with(testSecurityContext()));

		resultAction.andExpect(status().isOk()).andExpect(
				content().contentType(reportMakerParam.getMimeType()));

	}

	@Test
	public void testCommerceDownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_COMMERCE;

		String urlStr = String.format(
				"/api/reportService/commerce/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportService
				.getReportMakerParam(reportParamsetId);

		List<Long> contObjectIds = reportMakerParam.getContObjectList()
				.subList(0, 1);

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("contObjectIds",
						arrayToString(Longs.toArray(contObjectIds)));
			}
		};

		ResultActionsTester tester = new ResultActionsTester() {

			@Override
			public void testResultActions(ResultActions resultActions)
					throws Exception {

				resultActions.andExpect(content().contentType(
						reportMakerParam.getReportParamset()
								.getOutputFileType().getMimeType()));

			}
		};

		testJsonUpdate(urlStr, reportMakerParam.getReportParamset(),
				extraInitializer, tester);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConsT1DownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T1;

		String urlStr = String.format("/api/reportService/cons_t1/%d/download",
				reportParamsetId);

		ReportMakerParam reportMakerParam = reportService
				.getReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				// builder.param("contObjectIds",
				// arrayToString(Longs.toArray(contObjectIds)));
			}
		};

		ResultActionsTester tester = new ResultActionsTester() {

			@Override
			public void testResultActions(ResultActions resultActions)
					throws Exception {

				resultActions.andExpect(content().contentType(
						reportMakerParam.getMimeType()));

			}
		};

		testJsonUpdate(urlStr, reportMakerParam.getReportParamset(),
				extraInitializer, tester);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConsT2DownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T2;

		String urlStr = String.format("/api/reportService/cons_t2/%d/download",
				reportParamsetId);

		ReportMakerParam reportMakerParam = reportService
				.getReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				// builder.param("contObjectIds",
				// arrayToString(Longs.toArray(contObjectIds)));
			}
		};

		ResultActionsTester tester = new ResultActionsTester() {

			@Override
			public void testResultActions(ResultActions resultActions)
					throws Exception {

				resultActions.andExpect(content().contentType(
						reportMakerParam.getMimeType()));

			}
		};

		testJsonUpdate(urlStr, reportMakerParam.getReportParamset(),
				extraInitializer, tester);

	}

	@Test
	public void testEventDownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_EVENT;

		String urlStr = String.format("/api/reportService/event/%d/download",
				reportParamsetId);

		ReportMakerParam reportMakerParam = reportService
				.getReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				// builder.param("contObjectIds",
				// arrayToString(Longs.toArray(contObjectIds)));
			}
		};

		ResultActionsTester tester = new ResultActionsTester() {

			@Override
			public void testResultActions(ResultActions resultActions)
					throws Exception {

				resultActions.andExpect(content().contentType(
						reportMakerParam.getMimeType()));

			}
		};

		testJsonUpdate(urlStr, reportMakerParam.getReportParamset(),
				extraInitializer, tester);

	}

}
