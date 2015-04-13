package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportTemplateServiceTest extends JpaSupportTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testReportTemplateCreateDelete() {
		ReportTemplate rt = new ReportTemplate();
		rt.set_active(true);
		rt.setReportType(ReportTypeKeys.COMMERCE_REPORT);
		rt.setName("Коммерческий отчет");
		rt.setDescription("Тест " + System.currentTimeMillis());
		rt.setSubscriber(currentSubscriberService.getSubscriber());
		ReportTemplate result = reportTemplateService.createOne(rt);
		reportTemplateService.deleteOne(result);
	}

	@Test
	public void testDefaultCommerceReport() {
		List<ReportTemplate> resultList = reportTemplateService
				.getDefaultReportTemplates(ReportTypeKeys.COMMERCE_REPORT,
						DateTime.now());
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	public void testSubscriberReports() {
		List<ReportTemplate> resultList = reportTemplateService
				.getSubscriberReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKeys.COMMERCE_REPORT, DateTime.now());
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}
}
