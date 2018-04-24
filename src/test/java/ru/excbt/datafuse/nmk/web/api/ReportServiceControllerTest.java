package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.google.common.primitives.Longs;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.data.service.ReportMakerParamService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;



/**
 * TODO FIX
 */
@Transactional
public class ReportServiceControllerTest extends AnyControllerTest {

	public final static String API_REPORT_URL = "/api/reportService";

	private final static long TEST_PARAMSET_COMMERCE = 28618264;
	private final static long TEST_PARAMSET_COMMERCE2 = 38623152;
	private final static long TEST_PARAMSET_CONS_T1 = 29457574;
	private final static long TEST_PARAMSET_CONS_T2 = 28841247;
	private final static long TEST_PARAMSET_EVENT = 28820616;

	private static final Logger logger = LoggerFactory.getLogger(ReportServiceControllerTest.class);

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private ReportService reportService;

	private void redirectOption(ReportOutputFileType reportType) throws Exception {
		assertNotNull(reportType);

		String urlStr = String.format(API_REPORT_URL + "/commerce/%d/%s", 18811505, reportType.toLowerName());

		ResultActions resultAction = mockMvc.perform(get(urlStr).contentType(MediaType.APPLICATION_JSON)
				.param("beginDate", "2013-03-01").param("endDate", "2013-03-31").with(testSecurityContext()));

		resultAction.andDo(MockMvcResultHandlers.print());

		String redirectedUrl = resultAction.andReturn().getResponse().getRedirectedUrl();
		assertNotNull(redirectedUrl);

		logger.debug("testReportHtmlRedirect. Redirected: {}", redirectedUrl);

	}

	/*

	 */
	@Test
    @Transactional
	public void testCommerceDownloadGet() throws Exception {
		long reportParamsetId = 28618264;
		String urlStr = String.format("/api/reportService/commerce/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		ResultActions resultAction = mockMvc
				.perform(get(urlStr).contentType(MediaType.APPLICATION_JSON).with(testSecurityContext()));

		resultAction.andExpect(status().isOk()).andExpect(content().contentType(reportMakerParam.getMimeType()));

	}

	/*

	 */
	@Test
    @Transactional
	public void testEventDownloadGetPreview() throws Exception {
		long reportParamsetId = TEST_PARAMSET_EVENT;
		String urlStr = String.format("/api/reportService/event/%d/preview", reportParamsetId);

		_testGetHtml(urlStr);

	}

	/*

	 */
	@Ignore
	@Test
    @Transactional
	public void testConsT1DownloadGetPreview() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T1;
		String urlStr = String.format("/api/reportService/cons_t1/%d/preview", reportParamsetId);

		_testGetHtml(urlStr);

	}

	/*

	 */
	@Ignore
	@Test
    @Transactional
	public void testConsT2DownloadGetPreview() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T2;
		String urlStr = String.format("/api/reportService/cons_t2/%d/preview", reportParamsetId);

		_testGetHtml(urlStr);

	}

	/*

	 */
	@Test
    @Transactional
	public void testCommerceDownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_COMMERCE;

		String urlStr = String.format("/api/reportService/commerce/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		reportMakerParam.getReportParamset().setOutputFileType(ReportOutputFileType.PDF);

		List<Long> contObjectIds = reportMakerParam.getReportContObjectIdList().subList(0, 1);
		reportMakerParam.getReportParamset().setOutputFileZipped(true);

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("contObjectIds", TestUtils.arrayToString(Longs.toArray(contObjectIds)));
			}
		};

        ResultActionsTester tester = getResultActionsTester(reportMakerParam);

        _testUpdateJson(urlStr, reportMakerParam.getReportParamset(), extraInitializer, tester);

	}

	/*

	 */
    private ResultActionsTester getResultActionsTester(final ReportMakerParam reportMakerParam) {
        return (resultActions) -> {

                    resultActions.andExpect(content().contentType(reportMakerParam.getMimeType()));

                    byte[] resultBytes = resultActions.andReturn().getResponse().getContentAsByteArray();

                    logger.info("ResultBytes size:{}", resultBytes.length);

                    String filename = "./out/testCommerceDownloadPut" + reportMakerParam.getExt();

                    writeResultBytesToFile(filename, resultBytes);
            };
    }

    /*

     */
	@Test
    @Transactional
	public void testCommerceDownloadContext() throws Exception {

		long reportParamsetId = TEST_PARAMSET_COMMERCE;

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);
		List<Long> contObjectIds = reportMakerParam.getReportContObjectIdList().subList(0, 1);

		String urlStr = String.format("/api/reportService/commerce/%d/context/%d", reportParamsetId,
				contObjectIds.get(0));

        ResultActionsTester tester = getResultActionsTester(reportMakerParam);

        _testGet(urlStr, null, tester);

	}

	/*

	 */
	@Test
    @Transactional
	public void testCommerceDownloadContextPreview() throws Exception {

		long reportParamsetId = TEST_PARAMSET_COMMERCE;

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);
		List<Long> contObjectIds = reportMakerParam.getReportContObjectIdList().subList(0, 1);

		String urlStr = String.format("/api/reportService/commerce/%d/contextPreview/%d", reportParamsetId,
				contObjectIds.get(0));

		_testGetHtml(urlStr);

	}

	/*

	 */
	@Ignore
	@Test
    @Transactional
	public void testCommerceDownload__Html() throws Exception {
		long srcParamsetId = TEST_PARAMSET_COMMERCE;
		long modReportParamsetId = TEST_PARAMSET_COMMERCE2;

		String urlStr = String.format("/api/reportService/%d/download/html", modReportParamsetId);

		ReportMakerParam modReportMakerParam = reportMakerParamService.newReportMakerParam(modReportParamsetId);

		ReportMakerParam srcParam = reportMakerParamService.newReportMakerParam(srcParamsetId);

		List<Long> contObjectIds = srcParam.getReportContObjectIdList().subList(0, 1);

		modReportMakerParam.getReportParamset().setReportPeriod(srcParam.getReportParamset().getReportPeriod());
		modReportMakerParam.getReportParamset()
				.setParamsetStartDate(srcParam.getReportParamset().getParamsetStartDate());
		modReportMakerParam.getReportParamset().setParamsetEndDate(srcParam.getReportParamset().getParamsetEndDate());

		modReportMakerParam.getReportParamset().setOutputFileType(ReportOutputFileType.HTML);
		// modReportMakerParam.getReportParamset().

		RequestExtraInitializer extraInitializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("contObjectIds", TestUtils.arrayToString(Longs.toArray(contObjectIds)));
			}
		};

		ResultActionsTester tester = (resultActions) -> {
				resultActions.andExpect(content().contentType(ReportOutputFileType.HTML.getMimeType()));

				byte[] resultBytes = resultActions.andReturn().getResponse().getContentAsByteArray();

				logger.info("ResultBytes size:{}", resultBytes.length);

				String filename = "./out/testCommerceDownload__Html" + ReportOutputFileType.HTML.getExt();

				writeResultBytesToFile(filename, resultBytes);

		};

		_testUpdateJson(urlStr, modReportMakerParam.getReportParamset(), extraInitializer, tester);

	}

	/*

	 */
	@Ignore
	@Test
    @Transactional
	public void testConsT1DownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T1;

		String urlStr = String.format("/api/reportService/cons_t1/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = (builder) -> {

        } ;

		ResultActionsTester tester = (resultActions) -> {
				resultActions.andExpect(content().contentType(reportMakerParam.getMimeType()));
		};

		_testUpdateJson(urlStr, reportMakerParam.getReportParamset(), extraInitializer, tester);

	}

	/*

	 */
	@Ignore
	@Test
    @Transactional
	public void testConsT2DownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_CONS_T2;

		String urlStr = String.format("/api/reportService/cons_t2/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = (builder) -> {};

		ResultActionsTester tester = (resultActions) -> {
				resultActions.andExpect(content().contentType(reportMakerParam.getMimeType()));
		};

		_testUpdateJson(urlStr, reportMakerParam.getReportParamset(), extraInitializer, tester);

	}

	@Test
    @Transactional
	public void testEventDownloadPut() throws Exception {
		long reportParamsetId = TEST_PARAMSET_EVENT;

		String urlStr = String.format("/api/reportService/event/%d/download", reportParamsetId);

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(reportParamsetId);

		RequestExtraInitializer extraInitializer = (builder) -> {};

		ResultActionsTester tester = (resultActions) ->{
				resultActions.andExpect(content().contentType(reportMakerParam.getMimeType()));
		};

		_testUpdateJson(urlStr, reportMakerParam.getReportParamset(), extraInitializer, tester);

	}

	/*

	 */
	private void writeResultBytesToFile(String filename, byte[] fileBytes) throws FileNotFoundException, IOException {
		logger.info("Writing file: {}", filename);
		logger.info("fileBytes size: {}", fileBytes.length);

		try (FileOutputStream out = new FileOutputStream(filename)) {
			out.write(fileBytes);
			out.close();
		}

	}
}
