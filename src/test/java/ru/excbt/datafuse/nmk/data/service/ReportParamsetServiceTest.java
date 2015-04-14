package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportPeriodKey;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;

public class ReportParamsetServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetServiceTest.class);
	
	private static final long TEMPLATE_PARAMSET_ID = 28344056;
	
	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportPeriodService reportPeriodService;
	
	@Test
	public void testSelectReportParamset() {
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(ReportTypeKey.COMMERCE_REPORT, true);
		assertTrue(reportParamsetList.size() > 0);
		for (ReportParamset rp : reportParamsetList) {
			logger.info("id : {}. {}", rp.getId(), rp.getReportTemplate().getReportType().name());
		}
	}
	
	
	@Test
	public void testCreateByTemplateAndArchive () {
		ReportParamset rp = new ReportParamset();
		rp.setName("Created by template id=" + TEMPLATE_PARAMSET_ID);
		rp.setActiveStartDate(new Date());
		rp.setReportPeriod(reportPeriodService.findByKeyname(ReportPeriodKey.TODAY));
		ReportParamset result = reportParamsetService.createByTemplate(TEMPLATE_PARAMSET_ID, rp);
		assertNotNull(result);
		
		reportParamsetService.moveToArchive(result.getId());
	}
	
	
}
