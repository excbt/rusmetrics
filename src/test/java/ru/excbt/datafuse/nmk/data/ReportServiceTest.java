package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.ReportService;

public class ReportServiceTest extends JpaConfigTest {

	private final static String EXPECTED_URL = "/jasperserver/rest_v2/reports/PublicReports/nmk/common/commerce_report.html?"
			+ "end_date=2014-03-31&begin_date=2014-03-01&object_id=18811505";

	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceTest.class);

	@Autowired
	private ReportService reportService;

	@Test
	public void testReportService() {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTime beginDate = formatter.parseDateTime("01/03/2014");
		DateTime endDate = formatter.parseDateTime("31/03/2014");

		String resultStr = reportService.getCommercialReportHtmlPath(18811505,
				beginDate, endDate);

		logger.debug("Result Report URL: {}", resultStr);

		assertEquals(EXPECTED_URL, resultStr);
	}
	
	@Test
	public void testExternalServerSettings() {
		if (reportService.externalJasperServerEnable()) {
			String serverUrl = reportService.externalJasperServerUrl();
			assertNotNull(serverUrl);
		}
		
	}

}
