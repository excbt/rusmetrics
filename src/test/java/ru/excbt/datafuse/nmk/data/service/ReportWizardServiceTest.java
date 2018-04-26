package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ReportMasterTemplateBody;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportColumnSettings;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

@RunWith(SpringRunner.class)
public class ReportWizardServiceTest extends PortalDataTest {

	@Autowired
	private ReportWizardService reportWizardService;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Autowired
	private ReportTemplateService reportTemplateService;


	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


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
				new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
		checkNotNull(result);

		ReportTemplateBody templateBody = reportTemplateService
				.getReportTemplateBody(reportTemplate.getId());


		checkNotNull(templateBody);
		checkNotNull(templateBody.getBodyCompiled());


		reportTemplateService.deleteOne(result);
	}

}
