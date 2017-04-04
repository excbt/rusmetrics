package ru.excbt.datafuse.nmk.cli;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.config.DefaultProfileUtil;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.service.ReportMasterTemplateBodyService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportSystem;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class, DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class})
@ComponentScan(basePackages = {"ru.excbt.datafuse.nmk.config.jpa", "ru.excbt.datafuse.nmk.config.ldap"},
               basePackageClasses = {ReportMasterTemplateCli.class})
public class ReportMasterTemplateCli {

	private static final Logger log = LoggerFactory
			.getLogger(ReportMasterTemplateCli.class);

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateService reportTemplateService;

    @Inject
    private Environment env;

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(ReportMasterTemplateCli.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.setWebEnvironment(false);
        Environment env = app.run(args).getEnvironment();


//		ReportMasterTemplateCli app = new ReportMasterTemplateCli();


//		log.info("All Reports was loaded successfully");
//		System.out.println();
//		System.out.println("All Reports was loaded successfully");
//		System.out.println();
	}

    @PostConstruct
    public void initApplication() throws  IOException {

        log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
            && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run "
                + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
            && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not"
                + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }

        log.info("======================================================================");
        log.info("====== UPDATE TEMPLATES ==============================================");
        loadAllReportMasterTemplates(ReportConstants.IS_COMPILED);
        loadAllReportMasterTemplates(ReportConstants.IS_NOT_COMPILED);
        updateAllCommonReportTemplate();
        log.info("====== TEMPLATES UPDATED =============================================");
        log.info("======================================================================");

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

		log.info("Loading {} from file:{}...", reportTypeKey.name(),
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

		log.info("Report {} was successfully loaded from fils '{}'",
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

		loadReportMasterTemplate(ReportTypeKey.EL_QUALITY_REPORT,
				ReportConstants.Files.EL_QUALITY_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(
				ReportTypeKey.ELECTRIC_CONSUMPTION_ABONENT_REPORT,
				ReportConstants.Files.ELECTRIC_CONSUMPTION_ABONENT_FILE_COMPILED,
				isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_LAST_EVENT_REPORT,
				ReportConstants.Files.HW_LAST_EVENT_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CW_CONSUMPTION_REPORT,
				ReportConstants.Files.CW_CONSUMPTION_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_CALC_CONSUMPTION_BY_AVG_REPORT,
				ReportConstants.Files.HW_CALC_CONSUMPTION_BY_AVG_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HEAT_AVG_FORECAST_REPORT,
				ReportConstants.Files.HEAT_AVG_FORECAST_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.COMMERCE_ZPOINT_REPORT,
				ReportConstants.Files.COMM_ZPOINT_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_DATA_ZPOINT_REPORT,
				ReportConstants.Files.HW_DATA_ZPOINT_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.CONSOLIDATED_ZPOINT_REPORT,
				ReportConstants.Files.CONSOLIDATED_ZPOINT_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HEAT_QUALITY_CHART_REPORT,
				ReportConstants.Files.HEAT_QUALITY_CHART_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_BAD_PERIODS_REPORT,
				ReportConstants.Files.HW_BAD_PERIODS_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_1_REPORT,
				ReportConstants.Files.HW_QUALITY_1_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.HW_QUALITY_2_REPORT,
				ReportConstants.Files.HW_QUALITY_2_FILE_COMPILED, isCompiled);

		loadReportMasterTemplate(ReportTypeKey.RMA_ZPOINT_STAT_REPORT,
				ReportConstants.Files.RMA_ZPOINT_STAT_FILE_COMPILED, isCompiled);

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
		updateAnyCommonReportTemplate(ReportTypeKey.EL_QUALITY_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.ELECTRIC_CONSUMPTION_ABONENT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_LAST_EVENT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CW_CONSUMPTION_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_CALC_CONSUMPTION_BY_AVG_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HEAT_AVG_FORECAST_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.COMMERCE_ZPOINT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_DATA_ZPOINT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.CONSOLIDATED_ZPOINT_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HEAT_QUALITY_CHART_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_BAD_PERIODS_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_1_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.HW_QUALITY_2_REPORT);
		updateAnyCommonReportTemplate(ReportTypeKey.RMA_ZPOINT_STAT_REPORT);

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
			log.info(
					"Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					reportTypeKey);
			reportTemplateService.createCommonReportTemplate(reportTypeKey);
		}
	}

}
