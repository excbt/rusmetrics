package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportOutputFileType;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportSheduleTypeKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportSheduleServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportSheduleServiceTest.class);

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testSelectShedule() {
		List<ReportShedule> resultList = reportSheduleService
				.selectReportShedule(currentSubscriberService.getSubscriberId(), LocalDateTime.now()
						);

		assertNotNull(resultList);
	}

	@Test
	public void testAddNewSheduleClear() {

		List<ReportTemplate> reportTemplates = reportTemplateService
				.selectSubscriberReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());

		assertTrue(reportTemplates.size() > 0);

		ReportTemplate sReportTemplate = reportTemplates.get(0);

		logger.info("Found ReportTemplate (id={})", sReportTemplate.getId());

		ReportParamset sReportParamset = null;
		List<ReportParamset> reportParamsetList = reportParamsetService
				.selectReportParamset(sReportTemplate.getId(), DateTime.now());

		if (reportParamsetList.size() == 0) {
			sReportParamset = reportParamsetService.createReportParamsetMaster(
					sReportTemplate.getId(), "Auto Genereate for TEST",
					ReportPeriodKey.CURRENT_MONTH, ReportOutputFileType.PDF,
					currentSubscriberService.getSubscriberId());
		} else {
			sReportParamset = reportParamsetList.get(0);
		}

		ReportShedule reportShedule = new ReportShedule();
		reportShedule.setSubscriber(currentSubscriberService.getSubscriber());
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
