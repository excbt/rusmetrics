package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants;
import ru.excbt.datafuse.nmk.data.service.SystemParamService;

public class SystemParamServiceTest extends JpaConfigTest {

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
