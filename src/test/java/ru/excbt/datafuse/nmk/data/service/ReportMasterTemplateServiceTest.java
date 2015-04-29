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
	private static final String COMM_FILE_JRXML = "jasper/nmk_com_report.jrxml";

	private static final String EVENT_FILE_COMPILED = "jasper/nmk_event_report.jasper";
	private static final String CONS_T1_FILE_COMPILED = "jasper/nmk_consolidated_report_1.jasper";
	private static final String CONS_T2_FILE_COMPILED = "jasper/nmk_consolidated_report_2.jasper";

	private static final boolean IS_COMPILED = true;
	private static final boolean IS_NOT_COMPILED = false;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	/**
	 * 
	 * @param reportTypeKey
	 * @param fileResourceString
	 * @param isBodyCompiled
	 * @throws IOException
	 */
	public void testLoadReportMasterTemplate(ReportTypeKey reportTypeKey,
			String fileResourceString, boolean isBodyCompiled)
			throws IOException {
		ReportMasterTemplateBody templateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);
		if (templateBody == null) {
			templateBody = reportMasterTemplateBodyService
					.createOne(reportTypeKey);
			assertNotNull(templateBody);
		}

		boolean res = false;
		res = reportMasterTemplateBodyService.saveReportMasterTemplateBody(
				templateBody.getId(), fileResourceString, isBodyCompiled);

		assertTrue(res);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadCommerceMasterReportTemplateCompiled() throws IOException {
		
		testLoadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT,
				COMM_FILE_COMPILED, IS_COMPILED);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadCommerceMasterReportTemplateJrxml() throws IOException {
		
		testLoadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT,
				COMM_FILE_JRXML, IS_NOT_COMPILED);
	}

	
	@Test
	public void testLoadEventReport() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.EVENT_REPORT,
				EVENT_FILE_COMPILED, IS_COMPILED);
	}

	@Test
	public void testLoadContT1Report() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.CONS_T1_REPORT,
				CONS_T1_FILE_COMPILED, IS_COMPILED);
	}

	@Test
	public void testLoadContT2Report() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.CONS_T2_REPORT,
				CONS_T2_FILE_COMPILED, IS_COMPILED);
	}

}
