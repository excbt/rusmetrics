package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;

public class ReportMasterTemplateServiceTest extends JpaSupportTest {

	private static final String COMM_FILE_COMPILED = "jasper/nmk_com_report.jasper";
	private static final String COMM_FILE = "jasper/nmk_com_report.jrxml";

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;


	@Test
	public void testCreateCommerceReportMasterTemplate() throws IOException {
		ReportMasterTemplateBody templateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT);
		if (templateBody == null) {
			templateBody = reportMasterTemplateBodyService
					.createOne(ReportTypeKey.COMMERCE_REPORT);
			assertNotNull(templateBody);
		}

		boolean res = false;
		res = reportMasterTemplateBodyService.saveReportMasterTemplateBody(
				templateBody.getId(), COMM_FILE, false);
		assertTrue(res);
		res = reportMasterTemplateBodyService.saveReportMasterTemplateBody(
				templateBody.getId(), COMM_FILE_COMPILED, true);
		assertTrue(res);
	}

}
