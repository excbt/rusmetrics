package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import javax.transaction.Transactional;

public class ReportSettingControllerTest extends AnyControllerTest {

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
    @Transactional
	public void testGetReportTypes() throws Exception {
		_testGetJson("/api/reportSettings/reportType");
	}

	@Test
    @Transactional
	public void testGetReportPeriod() throws Exception {
		_testGetJson("/api/reportSettings/reportPeriod");
	}

	@Test
    @Transactional
	public void testGetReportSheduleType() throws Exception {
		_testGetJson("/api/reportSettings/reportSheduleType");
	}

	@Test
    @Transactional
	public void testGetReportActionType() throws Exception {
		_testGetJson("/api/reportSettings/reportActionType");
	}


}
