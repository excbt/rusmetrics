package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportSheduleTypeKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class ReportSheduleServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportSheduleServiceTest.class);

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Test
	public void testSelectShedule() {
		List<ReportShedule> resultList = reportSheduleService
				.selectReportShedule(portalUserIdsService.getCurrentIds().getSubscriberId(), LocalDateTime.now());

		assertNotNull(resultList);
	}

	@Test
    @Ignore
	public void testAddNewSheduleClear() {

		List<ReportTemplate> reportTemplates = reportTemplateService.selectSubscriberReportTemplates(
				ReportTypeKey.COMMERCE_REPORT_M_V, true, portalUserIdsService.getCurrentIds().getSubscriberId());

		assertTrue(reportTemplates.size() > 0);

		ReportTemplate sReportTemplate = reportTemplates.get(0);

		logger.info("Found ReportTemplate (id={})", sReportTemplate.getId());

		ReportParamset sReportParamset = null;
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportParamset(sReportTemplate.getId(),
				DateTime.now());

		if (reportParamsetList.size() == 0) {
			sReportParamset = reportParamsetService.createReportParamsetEx(sReportTemplate.getId(),
					"Auto Genereate for TEST", ReportPeriodKey.CURRENT_MONTH, ReportOutputFileType.PDF,
					portalUserIdsService.getCurrentIds().getSubscriberId(), true);
		} else {
			sReportParamset = reportParamsetList.get(0);
		}

		ReportShedule reportShedule = new ReportShedule();
		reportShedule.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
		reportShedule.setReportTemplate(sReportTemplate);
		reportShedule.setReportParamset(sReportParamset);
		reportShedule.setReportSheduleTypeKey(ReportSheduleTypeKey.DAILY);
		reportShedule.setSheduleTimeTemplate("0 11 * * *");
		reportShedule.setSheduleStartDate(new Date());

		ReportShedule result = reportSheduleService.createOne(reportShedule);
		assertNotNull(result);

		reportSheduleService.deleteOne(result.getId());
	}
}
