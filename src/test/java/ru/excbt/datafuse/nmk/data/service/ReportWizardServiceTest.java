package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

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
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ReportWizardServiceTest extends JpaSupportTest {

	@Autowired
	private ReportWizardService reportWizardService;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 */
	@Test
	public void testCreateReportWizard() {

		ReportMasterTemplateBody reportMasterTemplateBody = reportMasterTemplateBodyService
				.selectReportMasterTemplate(ReportTypeKey.COMMERCE_REPORT);

		checkNotNull(reportMasterTemplateBody);



		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setComment("Created By Wizard");
		reportTemplate.setActiveStartDate(new Date());
		reportTemplate.set_active(true);

		ReportColumnSettings reportColumnSettings = reportWizardService
				.getReportColumnSettings();

		ReportTemplate result = reportWizardService.createCommerceWizard(
				reportTemplate, reportColumnSettings,
				currentSubscriberService.getSubscriber());
		checkNotNull(result);

		ReportTemplateBody templateBody = reportTemplateService
				.getReportTemplateBody(reportTemplate.getId());


		checkNotNull(templateBody);
		checkNotNull(templateBody.getBodyCompiled());


		reportTemplateService.deleteOne(result);
	}

}
