package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportServiceTest extends JpaSupportTest {

	private final static String EXPECTED_URL = "/jasperserver/rest_v2/reports/PublicReports/nmk/common/nmk_com_report.html?"
			+ "end_date=2014-03-31&begin_date=2014-03-01&object_id=18811505";

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceTest.class);

	private final static long EVENT_TEST_PARAMSET_ID = 28820616;
	private final static long COMMERCE_TEST_PARAMSET_ID = 28618264;
	private final static long CONS_T1_TEST_PARAMSET_ID = 28820411;
	private final static long CONS_T2_TEST_PARAMSET_ID = 28841247;

	@Autowired
	private ReportService reportService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Test
	public void testReportService() {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTime beginDate = formatter.parseDateTime("01/03/2014");
		DateTime endDate = formatter.parseDateTime("31/03/2014");

		String resultStr = reportService.getCommercialReportPathHtml(18811505,
				beginDate, endDate);

		logger.debug("Result Report URL: {}", resultStr);

		assertEquals(EXPECTED_URL, resultStr);
	}

	@Test
	public void testExternalServerSettings() {
		if (reportService.externalJasperServerEnable()) {
			String serverUrl = reportService.externalJasperServerUrl();
			assertNotNull(serverUrl);
		} else {
			fail();
		}

	}

	@Test
	public void testMakeCommerceReport() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeCommerceReport.zip");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			// ByteArrayOutputStream os = new ByteArrayOutputStream();
			reportService.makeCommerceReportZip(28618264, LocalDateTime.now(),
					bos);
			// byte[] result = os.toByteArray();

			// assertNotNull(result);
			// assertTrue(result.length > 0);
			byte[] bytes = bos.toByteArray();
			assertNotNull(bytes);

			fos.write(bytes);

		} finally {
			fos.flush();
			fos.close();
		}
	}

	@Test
	public void testMakeEventReport() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeEventReport.zip");
		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);

		try {
			reportService.makeReport(EVENT_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriberId(),
					LocalDateTime.now(), zipOutputStream);
			zipOutputStream.close();
		} finally {
			fos.close();
		}
	}

	@Test
	public void testMakeComerceReport() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeCommerceReport.zip");
		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);

		try {
			reportService.makeReport(COMMERCE_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriberId(),
					LocalDateTime.now(), zipOutputStream);
			zipOutputStream.close();
		} finally {
			fos.close();
		}
	}
	
	@Test
	public void testMakeConsT1Report() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeConsT1Report.pdf");

		try {
			reportService.makeReport(CONS_T1_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriberId(),
					LocalDateTime.now(), fos);
		} finally {
			fos.close();
		}
	}	

	@Test
	public void testMakeConsT2Report() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeConsT2Report.pdf");
		
		try {
			reportService.makeReport(CONS_T2_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriberId(),
					LocalDateTime.now(), fos);
		} finally {
			fos.close();
		}
	}	
	

	@Test
	public void testReportEventsBody() throws IOException {
		testReportBody(EVENT_TEST_PARAMSET_ID,
				"jasper_reports/nmk_event_report.jasper",
				ReportTypeKey.EVENT_REPORT);
	}

	@Test
	public void testReportCommerceBody() throws IOException {
		testReportBody(COMMERCE_TEST_PARAMSET_ID,
				"jasper_reports/nmk_com_report.jasper",
				ReportTypeKey.COMMERCE_REPORT);
	}

	@Test
	public void testReportConsT1Body() throws IOException {
		testReportBody(CONS_T1_TEST_PARAMSET_ID,
				"jasper_reports/nmk_consolidated_report_1.jasper",
				ReportTypeKey.CONS_T1_REPORT);
	}

	@Test
	public void testReportConsT2Body() throws IOException {
		testReportBody(CONS_T2_TEST_PARAMSET_ID,
				"jasper_reports/nmk_consolidated_report_2.jasper",
				ReportTypeKey.CONS_T2_REPORT);
	}

	/**
	 * 
	 * @param reportParamsetId
	 * @param filename
	 * @throws IOException
	 */
	private void testReportBody(long reportParamsetId, String filename,
			ReportTypeKey reportTypeKey) throws IOException {
		ReportParamset paramset = reportParamsetService
				.findOne(reportParamsetId);
		assertNotNull(paramset);

		logger.info("ReportParamset : {}", reportParamsetId);
		logger.info("ReportTemplate : {}", paramset.getReportTemplate().getId());

		assertEquals(reportTypeKey, paramset.getReportTemplate()
				.getReportTypeKey());

		ReportTemplateBody rtBody = reportTemplateService
				.getReportTemplateBody(paramset.getReportTemplate().getId());

		assertNotNull(rtBody);

		FileInputStream is = new FileInputStream(filename);

		try {
			byte[] bytes = IOUtils.toByteArray(is);
			assertArrayEquals(bytes, rtBody.getBodyCompiled());
		} finally {
			is.close();
		}
	}

}
