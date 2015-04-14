package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKeys;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public class ReportParamsetServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetServiceTest.class);
	
	@Autowired
	private ReportParamsetService reportParamsetService;
	
	@Test
	public void testSelectReportParamset() {
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(ReportTypeKeys.COMMERCE_REPORT, true);
		assertTrue(reportParamsetList.size() > 0);
		for (ReportParamset rp : reportParamsetList) {
			logger.info("id : {}. {}", rp.getId(), rp.getReportTemplate().getReportType().name());
		}
	}
	
	
	
}
