package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ReportMasterTemplateServiceTest extends JpaSupportTest {

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	/**
	 *
	 * @param reportTypeKey
	 * @param fileResourceString
	 * @param isBodyCompiled
	 * @throws IOException
	 */
	private void testLoadReportMasterTemplate(ReportTypeKey reportTypeKey, String fileResourceString,
			boolean isBodyCompiled) throws IOException {
		ReportMasterTemplateBody templateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);
		if (templateBody == null) {
			templateBody = reportMasterTemplateBodyService.createOne(reportTypeKey);
			assertNotNull(templateBody);
		}

		boolean res = false;
		res = reportMasterTemplateBodyService.saveJasperReportMasterTemplateBody(templateBody.getId(),
				fileResourceString, isBodyCompiled);

		assertTrue(res);
	}

	/**
	 *
	 * @throws IOException
	 */
	@Test
    @Transactional
	public void testLoadCommerce_MasterReportTemplateCompiled() throws IOException {

		testLoadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT, ReportConstants.Files.COMM_FILE_COMPILED,
				ReportConstants.IS_COMPILED);
	}

	/**
	 *
	 * @throws IOException
	 */
	@Test
    @Transactional
	public void testLoadCommerce_MasterReportTemplateJrxml() throws IOException {

		testLoadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT, ReportConstants.Files.COMM_FILE_JRXML,
				ReportConstants.IS_NOT_COMPILED);
	}

	@Test
    @Transactional
	public void testLoadEvent_MasterReportTemplate() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.EVENT_REPORT, ReportConstants.Files.EVENT_FILE_COMPILED,
				ReportConstants.IS_COMPILED);
	}

	@Test
    @Transactional
	public void testLoadConsT1_MasterReportTemplate() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.CONS_T1_REPORT, ReportConstants.Files.CONS_T1_FILE_COMPILED,
				ReportConstants.IS_COMPILED);
	}

	@Test
    @Transactional
	public void testLoadConsT2_MasterReportTemplate() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.CONS_T2_REPORT, ReportConstants.Files.CONS_T2_FILE_COMPILED,
				ReportConstants.IS_COMPILED);
	}

	@Test
    @Transactional
	public void testLoadMetrological_MasterReportTemplate() throws Exception {
		testLoadReportMasterTemplate(ReportTypeKey.METROLOGICAL_REPORT,
				ReportConstants.Files.METROLOGICAL_FILE_COMPILED, ReportConstants.IS_COMPILED);
	}

}
