package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportServiceTest extends JpaSupportTest {

	private final static String EXPECTED_URL = "/jasperserver/rest_v2/reports/PublicReports/nmk/common/nmk_com_report.html?"
			+ "end_date=2014-03-31&begin_date=2014-03-01&object_id=18811505";

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceTest.class);

	private final static long EVENT_TEST_PARAMSET_ID = 28820616;
	
	@Autowired
	private ReportService reportService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

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
	@Ignore
	public void testMakeEventReport() throws IOException {
		FileOutputStream fos = new FileOutputStream(
				"./out/testMakeEventReport.pdf");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			// ByteArrayOutputStream os = new ByteArrayOutputStream();
			reportService.makeEventsReportPdf(EVENT_TEST_PARAMSET_ID,
					currentSubscriberService.getSubscriber().getOrganization()
							.getId(), LocalDateTime.now(), bos);

			byte[] bytes = bos.toByteArray();
			assertNotNull(bytes);

			fos.write(bytes);

		} finally {
			fos.flush();
			fos.close();
		}
	}

}
