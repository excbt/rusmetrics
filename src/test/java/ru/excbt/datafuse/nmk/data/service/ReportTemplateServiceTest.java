package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;

public class ReportTemplateServiceTest extends JpaSupportTest {

	@Autowired
	private ReportTemplateService reportTemplateService;
	
	@Test
	public void testReportTemplateCreate() {
		ReportTemplate rt = new ReportTemplate();
		rt.set_active(true);
		rt.setReportType(ReportTypeKeys.COMMERCE_REPORT);
		rt.setName("Коммерческий отчет");
		rt.setDescription("Тест " + System.currentTimeMillis());
		ReportTemplate result = reportTemplateService.createOne(rt);
		//reportTemplateService.deleteOne(result);
	}
}
