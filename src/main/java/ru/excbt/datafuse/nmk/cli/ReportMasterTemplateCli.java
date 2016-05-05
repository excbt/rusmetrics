package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.service.ReportMasterTemplateBodyService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportSystem;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public class ReportMasterTemplateCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportMasterTemplateCli.class);

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		ReportMasterTemplateCli app = new ReportMasterTemplateCli();

		app.autowireBeans();
		app.showAppStatus();
		app.loadAllReportMasterTemplates(ReportConstants.IS_COMPILED);
		app.loadAllReportMasterTemplates(ReportConstants.IS_NOT_COMPILED);
		app.updateAllCommonReportTemplate();

		logger.info("All Reports was loaded successfully");
		System.out.println();
		System.out.println("All Reports was loaded successfully");
		System.out.println();
	}

	/**
	 * 
	 * @param reportTypeKey
	 * @param fileResourceString
	 * @param isCompiled
	 * @throws IOException
	 */
	private void loadReportMasterTemplate(ReportTypeKey reportTypeKey,
			String fileResourceString, boolean isCompiled) throws IOException {

		String correctedFilename = FilenameUtils
				.removeExtension(fileResourceString)
				+ (isCompiled ? ReportConstants.EXT_JASPER
						: ReportConstants.EXT_JRXML);

		logger.info("Loading {} from file:{}...", reportTypeKey.name(),
				correctedFilename);

		ReportMasterTemplateBody templateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(reportTypeKey);
		if (templateBody == null) {
			templateBody = reportMasterTemplateBodyService
					.createOne(reportTypeKey);
			checkNotNull(templateBody);
		}

		boolean res = false;

		if (reportTypeKey.getReportSystem() == ReportSystem.JASPER) {
			res = reportMasterTemplateBodyService
					.saveJasperReportMasterTemplateBody(templateBody.getId(),
							correctedFilename, isCompiled);

		} else if (reportTypeKey.getReportSystem() == ReportSystem.PENTAHO) {
			res = reportMasterTemplateBodyService
					.savePentahoReportMasterTemplateBody(templateBody.getId(),
							correctedFilename, isCompiled);

		} else {
			new OperationNotSupportedException();
		}
		checkState(res);

		logger.info("Report {} was successfully loaded from fils '{}'",
				reportTypeKey.name(), correctedFilename);

		System.out.println();
		System.out.println(String.format(
				"Report %s was successfully loaded from fils '%s'",
				reportTypeKey.name(), correctedFilename));
		System.out.println();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void loadAllReportMasterTemplates(boolean isCompiled)
			throws IOException {
		loadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT,
				ReportConstants.Files.COMM_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.EVENT_REPORT,
				ReportConstants.Files.EVENT_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CONS_T1_REPORT,
				ReportConstants.Files.CONS_T1_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CONS_T2_REPORT,
				ReportConstants.Files.CONS_T2_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.METROLOGICAL_REPORT,
				ReportConstants.Files.METROLOGICAL_FILE_COMPILED, isCompiled); //

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_REPORT,
				ReportConstants.Files.CONSUMPTION_FILE_COMPILED, isCompiled); // deprecated

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_REPORT_V1_1,
				ReportConstants.Files.CONSUMPTION_V1_1_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(
				ReportTypeKey.CONSUMPTION_HISTORY_REPORT, // deprecated
				ReportConstants.Files.CONSUMPTION_HISTORY_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_HISTORY_REPORT_V2,
				ReportConstants.Files.CONSUMPTION_HISTORY_V2_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(
				ReportTypeKey.CONSUMPTION_HISTORY_ETALON_REPORT_V2,
				ReportConstants.Files.CONSUMPTION_HISTORY_ETALON_V2_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(
				// deprecated
				ReportTypeKey.CONSUMPTION_HISTORY_ETALON_REPORT,
				ReportConstants.Files.CONSUMPTION_HISTORY_ETALON_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CONSUMPTION_ETALON_REPORT,
				ReportConstants.Files.CONSUMPTION_ETALON_FILE_COMPILED,
				isCompiled); //

		loadReportMasterTemplate(ReportTypeKey.LOG_JOURNAL_REPORT,
				ReportConstants.Files.LOG_JOURNAL_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.ABONENT_SERVICE_REPORT,
				ReportConstants.Files.ABONENT_SERVICE_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.RMA_ABONENT_SERVICE_REPORT,
				ReportConstants.Files.RMA_ABONENT_SERVICE_FILE_COMPILED,
				isCompiled); //

		loadReportMasterTemplate(ReportTypeKey.PARTNER_SERVICE_REPORT,
				ReportConstants.Files.PARTNER_SERVICE_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.ELECTRIC_READINGS_REPORT,
				ReportConstants.Files.ELECTRIC_READINGS_FILE_COMPILED,
				isCompiled); //

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_REPORT,
				ReportConstants.Files.HW_QUALITY_FILE_COMPILED, isCompiled); //

		loadReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT_M_V,
				ReportConstants.Files.COMM_M_V_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.ELECTRIC_CONSUMPTION_REPORT,
				ReportConstants.Files.ELECTRIC_CONSUMPTION_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_SHEET_REPORT,
				ReportConstants.Files.HW_QUALITY_SHEET_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_SHEET_HOUR_REPORT,
				ReportConstants.Files.HW_QUALITY_SHEET_HOUR_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_ACT_1_REPORT,
				ReportConstants.Files.HW_QUALITY_ACT_1_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_ACT_2_REPORT,
				ReportConstants.Files.HW_QUALITY_ACT_2_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_DATA_REPORT,
				ReportConstants.Files.HW_DATA_FILE_COMPILED, isCompiled);

	}

	/**
	 * 
	 */
	private void updateAllCommonReportTemplate() {
		updateAnyCommonReportTemplate(ReportTypeKey.LOG_JOURNAL_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONS_T2_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.COMMERCE_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_ETALON_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_REPORT_V1_1);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_HISTORY_REPORT_V2);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_HISTORY_ETALON_REPORT_V2);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_HISTORY_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSUMPTION_HISTORY_ETALON_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONS_T1_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.EVENT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.METROLOGICAL_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.ABONENT_SERVICE_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.RMA_ABONENT_SERVICE_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.PARTNER_SERVICE_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.ELECTRIC_READINGS_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.COMMERCE_REPORT_M_V);
		updateAnyCommonReportTemplate(ReportTypeKey.ELECTRIC_CONSUMPTION_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_SHEET_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_SHEET_HOUR_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_ACT_1_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_ACT_2_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_DATA_REPORT);

	}

	/**
	 * 
	 * @param reportTypeKey
	 */
	private void updateAnyCommonReportTemplate(ReportTypeKey reportTypeKey) {
		int result = reportTemplateService.updateCommonReportTemplateBody(
				reportTypeKey, ReportConstants.IS_ACTIVE,
				ReportConstants.IS_COMPILED);

		if (result == 0) {
			logger.info(
					"Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					reportTypeKey);
			reportTemplateService.createCommonReportTemplate(reportTypeKey);
		}
	}

}
