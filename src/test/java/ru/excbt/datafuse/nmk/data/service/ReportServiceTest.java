package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
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
	public void testMakeEventReport() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeEventReport.zip");
		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);

		try {
			reportService.makeReport(EVENT_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriberId(),
					LocalDateTime.now(), zipOutputStream, true);
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
					LocalDateTime.now(), zipOutputStream, true);
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
					LocalDateTime.now(), fos, false);
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
					LocalDateTime.now(), fos, false);
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
