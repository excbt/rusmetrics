package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SystemParamServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SystemParamServiceTest.class);

	@Autowired
	private SystemParamService systemParamService;

	@Test
	public void test() {
		String reportValue = systemParamService
				.getParamValueAsString(ReportConstants.COMMERCIAL_REPORT_TEMPLATE_PATH);
		assertNotNull(reportValue);
		logger.debug("Default Commercial Report Path {}", reportValue);
	}

}
