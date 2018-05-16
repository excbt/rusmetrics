package ru.excbt.datafuse.nmk.data.service;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ReportServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportServiceTest.class);

	private final static long EVENT_TEST_PARAMSET_ID = 28820616;
	private final static long COMMERCE_TEST_PARAMSET_ID = 28618264;
	private final static long CONS_T1_TEST_PARAMSET_ID = 28820411;
	private final static long CONS_T2_TEST_PARAMSET_ID = 28841247;
	private final static long CONSUMPTION_PARAMSET_ID = 53141233;

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Test
	public void testMakeEventReport() throws IOException {

		try (FileOutputStream fos = new FileOutputStream("./out/testMakeEventReport.zip");) {

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(EVENT_TEST_PARAMSET_ID);

			reportService.makeReportByParamset(reportMakerParam, LocalDateTime.now(), fos);
		}
	}

	@Test
	public void testMakeComerceReport() throws IOException {

		try (FileOutputStream fos = new FileOutputStream("./out/testMakeCommerceReport.zip");) {

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(COMMERCE_TEST_PARAMSET_ID);

			reportService.makeReportByParamset(reportMakerParam, LocalDateTime.now(), fos);
		}
	}

	@Test
	public void testMakeConsT1Report() throws IOException {

		try (FileOutputStream fos = new FileOutputStream("./out/testMakeConsT1Report.pdf");) {

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(CONS_T1_TEST_PARAMSET_ID);

			reportService.makeReportByParamset(reportMakerParam, LocalDateTime.now(), fos);
		}
	}

	@Test
	public void testMakeConsT2Report() throws IOException {

		try (FileOutputStream fos = new FileOutputStream("./out/testMakeConsT2Report.pdf");) {

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(CONS_T2_TEST_PARAMSET_ID);

			reportService.makeReportByParamset(reportMakerParam, LocalDateTime.now(), fos);
		}
	}

	@Test
	public void testMakeConsumptionReport() throws IOException {

		try (FileOutputStream fos = new FileOutputStream("./out/testMakeConsupmtionReport.pdf");) {

			ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(CONSUMPTION_PARAMSET_ID);

			reportService.makeReportByParamset(reportMakerParam, LocalDateTime.now(), fos);
		}
	}

	@Test
    @Ignore
	public void testReportEventsBody() throws IOException {
		testReportBody(EVENT_TEST_PARAMSET_ID, "jasper_reports/nmk_event_report.jasper", ReportTypeKey.EVENT_REPORT);
	}

	@Test
	public void testReportCommerceBody() throws IOException {
		testReportBody(COMMERCE_TEST_PARAMSET_ID, "jasper_reports/nmk_com_report.jasper",
				ReportTypeKey.COMMERCE_REPORT, false);
	}

	@Test
	public void testReportConsT1Body() throws IOException {
		testReportBody(CONS_T1_TEST_PARAMSET_ID, "jasper_reports/nmk_consolidated_report_1.jasper",
				ReportTypeKey.CONS_T1_REPORT, false);
	}

	@Test
    @Ignore
	public void testReportConsT2Body() throws IOException {
		testReportBody(CONS_T2_TEST_PARAMSET_ID, "jasper_reports/nmk_consolidated_report_2.jasper",
				ReportTypeKey.CONS_T2_REPORT);
	}

	/**
	 *
	 * @param reportParamsetId
	 * @param filename
	 * @throws IOException
	 */
	private void testReportBody(long reportParamsetId, String filename, ReportTypeKey reportTypeKey, boolean checkBody)
			throws IOException {
		ReportParamset paramset = reportParamsetService.findReportParamset(reportParamsetId);
		assertNotNull(paramset);

		logger.info("ReportParamset : {}", reportParamsetId);
		logger.info("ReportTemplate : {}", paramset.getReportTemplate().getId());

		assertEquals(reportTypeKey.getKeyname(), paramset.getReportTemplate().getReportTypeKeyname());

		ReportTemplateBody rtBody = reportTemplateService.getReportTemplateBody(paramset.getReportTemplate().getId());

		assertNotNull(rtBody);

		FileInputStream is = new FileInputStream(filename);

		try {
			byte[] bytes = IOUtils.toByteArray(is);
			if (checkBody) assertArrayEquals(bytes, rtBody.getBodyCompiled());
		} finally {
			is.close();
		}
	}

    private void testReportBody(long reportParamsetId, String filename, ReportTypeKey reportTypeKey)
        throws IOException {
        testReportBody(reportParamsetId, filename,reportTypeKey,true);
    }

}
