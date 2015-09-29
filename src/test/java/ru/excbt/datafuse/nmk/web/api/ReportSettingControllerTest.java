package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class ReportSettingControllerTest extends AnyControllerTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testGetReportTypes() throws Exception {
		_testJsonGet("/api/reportSettings/reportType");
	}

	@Test
	public void testGetReportPeriod() throws Exception {
		_testJsonGet("/api/reportSettings/reportPeriod");
	}

	@Test
	public void testGetReportSheduleType() throws Exception {
		_testJsonGet("/api/reportSettings/reportSheduleType");
	}

	@Test
	public void testGetReportActionType() throws Exception {
		_testJsonGet("/api/reportSettings/reportActionType");
	}

	
}
