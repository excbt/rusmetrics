package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportSheduleServiceTest extends JpaSupportTest {

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testSelectShedule() {
		List<ReportShedule> resultList = reportSheduleService
				.selectReportShedule(
						currentSubscriberService.getSubscriberId(),
						DateTime.now());

		assertNotNull(resultList);
	}
}
