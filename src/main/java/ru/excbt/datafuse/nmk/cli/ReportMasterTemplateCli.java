package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.service.ReportMasterTemplateBodyService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public class ReportMasterTemplateCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportMasterTemplateCli.class);

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		ReportMasterTemplateCli app = new ReportMasterTemplateCli();

		app.autowireBeans();
		app.showAppStatus();
		app.loadAllCompiledReportMasterTemplates();
		app.loadAllJrxmlReportMasterTemplates();

		logger.info("All Reports was loaded successfully");
		System.out.println();
		System.out.println("All Reports was loaded successfully");
		System.out.println();
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @param fileResourceString
	 * @param isBodyCompiled
	 * @throws IOException
	 */
	private void loadReportMasterTemplate(ReportTypeKey reportTypeKey,
			String fileResourceString, boolean isBodyCompiled)
			throws IOException {

		logger.info("Loading {} from file:{}...", reportTypeKey.name(),
				fileResourceString);

		ReportMasterTemplateBody templateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);
		if (templateBody == null) {
			templateBody = reportMasterTemplateBodyService
					.createOne(reportTypeKey);
			checkNotNull(templateBody);
		}

		boolean res = false;
		res = reportMasterTemplateBodyService.saveReportMasterTemplateBody(
				templateBody.getId(), fileResourceString, isBodyCompiled);

		checkState(res);

		logger.info("Report {} was successfully loaded from fils '{}'",
				reportTypeKey.name(), fileResourceString);

		System.out.println();
		System.out.println(String.format(
				"Report %s was successfully loaded from fils '%s'",
				reportTypeKey.name(), fileResourceString));
		System.out.println();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void loadAllCompiledReportMasterTemplates() throws IOException {
		loadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT,
				ReportConstants.Paths.COMM_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.EVENT_REPORT,
				ReportConstants.Paths.EVENT_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.CONS_T1_REPORT,
				ReportConstants.Paths.CONS_T1_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.CONS_T2_REPORT,
				ReportConstants.Paths.CONS_T2_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.METROLOGICAL_REPORT,
				ReportConstants.Paths.METROLOGICAL_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_REPORT,
				ReportConstants.Paths.CONSUMPTION_FILE_COMPILED,
				ReportConstants.IS_COMPILED);

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_HISTORY_REPORT,
				ReportConstants.Paths.CONSUMPTION_HISTORY_FILE_COMPILED,
				ReportConstants.IS_COMPILED);
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void loadAllJrxmlReportMasterTemplates() throws IOException {

		loadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT,
				ReportConstants.Paths.COMM_FILE_JRXML,
				ReportConstants.IS_NOT_COMPILED);
	}

}
